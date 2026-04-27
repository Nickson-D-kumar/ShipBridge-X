package com.courier.service;

import com.courier.dto.BookCourierRequestDto;
import com.courier.dto.UpdateStatusRequestDto;
import com.courier.entity.CourierEntity;
import com.courier.entity.Employee;
import com.courier.entity.Payment;
import com.courier.exception.PaymentException;
import com.courier.exception.ResourceNotFoundException;
import com.courier.repository.CourierRepository;
import com.courier.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CourierService {

    private final CourierRepository courierRepo;
    private final PaymentRepository payRepo;
    private final EmployeeService   empService;

    public CourierService(CourierRepository courierRepo,
                          PaymentRepository payRepo,
                          EmployeeService empService) {
        this.courierRepo = courierRepo;
        this.payRepo     = payRepo;
        this.empService  = empService;
    }

    public List<CourierEntity> getAllCouriers()                 { return courierRepo.findAll(); }
    public List<CourierEntity> getCouriersBySender(String u)   { return courierRepo.findBySenderUsername(u); }
    public List<CourierEntity> getCouriersByEmployee(String u) { return courierRepo.findByEmpUsername(u); }

    public CourierEntity getById(Integer trackingId) {
        return courierRepo.findById(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Courier with tracking ID " + trackingId + " not found."));
    }

    /**
     * Original "Book Courier" flow preserved exactly:
     * 1. Find nearest FREE employee by pincode diff
     * 2. Create courier, cost = weight * 50
     * 3. Mark employee BUSY
     * 4. Validate payment (UPI/CARD/COD) — original rules
     * 5. Payment fail → mark employee FREE, throw PaymentException
     * 6. markPaymentPaid(), save courier + payment
     *
     * FIX: Also saves destinationPincode into the CourierEntity so it is
     * persisted in the database for future reference.
     */
    @Transactional
    public Map<String, Object> bookCourier(BookCourierRequestDto req) {
        Employee emp = empService.findNearest(req.getDestinationPincode());

        CourierEntity courier = new CourierEntity();
        courier.setReceiverName(req.getReceiverName());
        courier.setDestination(req.getDestination());
        courier.setDestinationPincode(req.getDestinationPincode()); // FIX: persist pincode
        courier.setWeight(req.getWeight());
        courier.setSenderUsername(req.getSenderUsername());
        courier.setEmpUsername(emp.getUsername());
        courier.setStatus("Booked");
        courier.setPaymentStatus("PENDING");
        courier.calculateCost();

        // Record booking date (dd-MM-yyyy)
        java.time.LocalDate today = java.time.LocalDate.now();
        courier.setBookingDate(today.getDayOfMonth() + "-" + today.getMonthValue() + "-" + today.getYear());

        empService.markBusy(emp.getUsername());

        String resolvedMethod;
        try {
            resolvedMethod = validatePayment(req.getPaymentMethod(),
                    req.getUpiId(), req.getCardNumber(), req.getCvv());
        } catch (PaymentException ex) {
            empService.markFree(emp.getUsername());
            throw ex;
        }

        courier.markPaymentPaid();
        CourierEntity saved = courierRepo.save(courier);

        LocalDateTime now = LocalDateTime.now();
        String ts = now.getDayOfMonth() + "-" + now.getMonthValue() + "-" + now.getYear()
                + " " + String.format("%02d:%02d", now.getHour(), now.getMinute());
        Payment payment = new Payment(saved.getTrackingId(), req.getSenderUsername(),
                                      saved.getCost(), resolvedMethod, ts);
        Payment savedPay = payRepo.save(payment);

        Map<String, Object> result = new HashMap<>();
        result.put("trackingId",    saved.getTrackingId());
        result.put("paymentId",     savedPay.getPaymentId());
        result.put("destination",   saved.getDestination());
        result.put("weight",        saved.getWeight());
        result.put("cost",          saved.getCost());
        result.put("paymentMethod", resolvedMethod);
        result.put("employee",      emp.getName() + " (" + emp.getPhone() + ")");
        result.put("courier",       saved);
        result.put("payment",       savedPay);
        return result;
    }

    /** Original UPI/CARD/COD validation logic. */
    private String validatePayment(String method, String upiId, String cardNumber, String cvv) {
        switch (method.toUpperCase()) {
            case "UPI":
                if (upiId == null || upiId.trim().isEmpty() || !upiId.contains("@"))
                    throw new PaymentException("Invalid UPI ID! Payment FAILED.");
                return "UPI";
            case "CARD":
                if (cardNumber == null || cardNumber.trim().isEmpty())
                    throw new PaymentException("Card number required!");
                String c = cardNumber.replaceAll("\\s+", "");
                if (c.length() != 16 || !c.matches("\\d+"))
                    throw new PaymentException("Invalid Card Number! Payment FAILED.");
                if (cvv == null || cvv.trim().length() != 3 || !cvv.trim().matches("\\d+"))
                    throw new PaymentException("Invalid CVV! Payment FAILED.");
                return "CARD";
            default:
                return "COD";
        }
    }

    /**
     * Original employee "Update Courier Status" logic:
     * Only the assigned employee can update. Delivered → employee FREE.
     */
    @Transactional
    public CourierEntity updateStatus(Integer trackingId, UpdateStatusRequestDto req) {
        CourierEntity courier = getById(trackingId);
        if (!courier.getEmpUsername().equals(req.getEmpUsername()))
            throw new ResourceNotFoundException("Tracking ID not found or not assigned to you.");
        courier.setStatus(req.getStatus());
        CourierEntity saved = courierRepo.save(courier);
        if (req.getStatus().trim().equalsIgnoreCase("Delivered")) {
            empService.markFree(req.getEmpUsername());
            // Record delivered date
            java.time.LocalDate today = java.time.LocalDate.now();
            saved.setDeliveredDate(today.getDayOfMonth() + "-" + today.getMonthValue() + "-" + today.getYear());
            courierRepo.save(saved);
        }
        return saved;
    }

    /**
     * Original customer "Track Courier" — validates ownership then returns report.
     */
    public Map<String, Object> getTrackingReport(Integer trackingId, String senderUsername) {
        CourierEntity c = getById(trackingId);
        if (!c.getSenderUsername().equals(senderUsername))
            throw new ResourceNotFoundException("Tracking ID not found or does not belong to you.");
        Map<String, Object> r = new HashMap<>();
        r.put("trackingId",        c.getTrackingId());
        r.put("receiverName",      c.getReceiverName());
        r.put("destination",       c.getDestination());
        r.put("weight",            c.getWeight() + " kg");
        r.put("cost",              "Rs." + c.getCost());
        r.put("paymentStatus",     c.getPaymentStatus());
        r.put("status",            c.getStatus());
        r.put("estimatedDelivery", c.getEstimatedDelivery());
        r.put("assignedEmployee",  c.getEmpUsername());
        return r;
    }
}

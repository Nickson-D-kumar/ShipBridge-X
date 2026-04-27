package com.courier.controller;

import com.courier.dto.BookCourierRequestDto;
import com.courier.dto.LoginRequestDto;
import com.courier.entity.CourierEntity;
import com.courier.entity.Payment;
import com.courier.entity.UserAccount;
import com.courier.exception.InvalidLoginException;
import com.courier.service.AuthService;
import com.courier.service.CourierService;
import com.courier.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * CustomerController — mirrors original Customer Panel menu exactly
 *
 * POST /api/customer/login
 * POST /api/customer/couriers                           — Book Courier
 * GET  /api/customer/{username}/couriers/{id}/track     — Track Courier
 * GET  /api/customer/{username}/couriers                — View My Couriers
 * GET  /api/customer/{username}/payments                — View My Payments
 */
@RestController
@RequestMapping("/api/customer")
@CrossOrigin(origins = "*")
public class CustomerController {

    private final AuthService    authService;
    private final CourierService courierService;
    private final PaymentService paymentService;

    public CustomerController(AuthService authService,
                               CourierService courierService,
                               PaymentService paymentService) {
        this.authService    = authService;
        this.courierService = courierService;
        this.paymentService = paymentService;
    }

    /** POST /api/customer/login */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> customerLogin(@Valid @RequestBody LoginRequestDto req) {
        UserAccount acc = authService.login(req.getUsername(), req.getPassword());
        if (!"CUSTOMER".equals(acc.getRole()))
            throw new InvalidLoginException("Invalid customer login!");
        return ResponseEntity.ok(Map.of(
                "username", acc.getUsername(),
                "role",     acc.getRole(),
                "message",  "Login successful"
        ));
    }

    /**
     * POST /api/customer/couriers — Book Courier (option 1)
     * Body: { senderUsername, receiverName, destination, destinationPincode,
     *          weight, paymentMethod, upiId?, cardNumber?, cvv? }
     * Returns: { trackingId, paymentId, cost, employee, ... }
     */
    @PostMapping("/couriers")
    public ResponseEntity<Map<String, Object>> bookCourier(
            @Valid @RequestBody BookCourierRequestDto req) {
        Map<String, Object> result = courierService.bookCourier(req);
        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/customer/{username}/couriers/{trackingId}/track — Track Courier (option 2)
     * Original: validates ownership; returns full tracking report
     */
    @GetMapping("/{username}/couriers/{trackingId}/track")
    public ResponseEntity<Map<String, Object>> trackCourier(
            @PathVariable String username,
            @PathVariable Integer trackingId) {
        return ResponseEntity.ok(courierService.getTrackingReport(trackingId, username));
    }

    /** GET /api/customer/{username}/couriers — View My Couriers (option 3) */
    @GetMapping("/{username}/couriers")
    public ResponseEntity<List<CourierEntity>> viewMyCouriers(@PathVariable String username) {
        return ResponseEntity.ok(courierService.getCouriersBySender(username));
    }

    /** GET /api/customer/{username}/payments — View My Payments (option 4) */
    @GetMapping("/{username}/payments")
    public ResponseEntity<List<Payment>> viewMyPayments(@PathVariable String username) {
        return ResponseEntity.ok(paymentService.getByCustomerUsername(username));
    }
}

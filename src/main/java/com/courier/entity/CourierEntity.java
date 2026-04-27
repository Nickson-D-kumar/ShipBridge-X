package com.courier.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "couriers")
public class CourierEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tracking_id")
    private Integer trackingId;

    @NotBlank
    @Column(name = "receiver_name", nullable = false)
    private String receiverName;

    @NotBlank
    @Column(name = "destination", nullable = false)
    private String destination;

    // FIX: Store destinationPincode in the entity/DB so it can be referenced later.
    // Previously the pincode was only used at booking time to find the nearest employee
    // and then discarded — it was never saved to the database.
    @Column(name = "destination_pincode")
    private Integer destinationPincode;

    @NotNull
    @Column(name = "weight", nullable = false)
    private Double weight;

    @Column(name = "status", nullable = false)
    private String status = "Booked";

    @Column(name = "cost", nullable = false)
    private Double cost;

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus = "PENDING";

    @Column(name = "sender_username", nullable = false)
    private String senderUsername;

    @Column(name = "emp_username", nullable = false)
    private String empUsername;

    @Column(name = "delivered_date")
    private String deliveredDate;

    @Column(name = "booking_date")
    private String bookingDate;

    // ── Original business logic preserved ────────────────────

    /** Cost formula: weight * 50  (original rule) */
    public void calculateCost() {
        this.cost = this.weight * 50.0;
    }

    public void markPaymentPaid() { this.paymentStatus = "PAID"; }

    /** Original estimated delivery logic preserved exactly */
    public String getEstimatedDelivery() {
        if (status == null) return "To be updated";
        switch (status.trim().toLowerCase()) {
            case "booked":           return "3-5 days";
            case "in transit":       return "2-3 days";
            case "out for delivery": return "Today";
            case "delivered":        return "Delivered";
            default:                 return "To be updated";
        }
    }

    // ── Getters ──────────────────────────────────────────────
    public Integer getTrackingId()        { return trackingId; }
    public String  getReceiverName()      { return receiverName; }
    public String  getDestination()       { return destination; }
    public Integer getDestinationPincode(){ return destinationPincode; }
    public Double  getWeight()            { return weight; }
    public String  getStatus()            { return status; }
    public Double  getCost()              { return cost; }
    public String  getPaymentStatus()     { return paymentStatus; }
    public String  getSenderUsername()    { return senderUsername; }
    public String  getEmpUsername()       { return empUsername; }
    public String  getDeliveredDate()     { return deliveredDate; }
    public String  getBookingDate()       { return bookingDate; }

    // ── Setters ──────────────────────────────────────────────
    public void setTrackingId(Integer trackingId)           { this.trackingId = trackingId; }
    public void setReceiverName(String receiverName)        { this.receiverName = receiverName; }
    public void setDestination(String destination)          { this.destination = destination; }
    public void setDestinationPincode(Integer p)            { this.destinationPincode = p; }
    public void setWeight(Double weight)                    { this.weight = weight; }
    public void setStatus(String status)                    { this.status = status; }
    public void setCost(Double cost)                        { this.cost = cost; }
    public void setPaymentStatus(String paymentStatus)      { this.paymentStatus = paymentStatus; }
    public void setSenderUsername(String u)                 { this.senderUsername = u; }
    public void setEmpUsername(String u)                    { this.empUsername = u; }
    public void setDeliveredDate(String d)                  { this.deliveredDate = d; }
    public void setBookingDate(String d)                    { this.bookingDate = d; }
}

package com.courier.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer paymentId;

    @NotNull
    @Column(name = "tracking_id", nullable = false)
    private Integer trackingId;

    @NotBlank
    @Column(name = "customer_username", nullable = false)
    private String customerUsername;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Double amount;

    @NotBlank
    @Column(name = "method", nullable = false)
    private String method;   // UPI | CARD | COD

    @Column(name = "status", nullable = false)
    private String status = "PAID";

    @Column(name = "timestamp")
    private String timestamp;

    public Payment() {}

    public Payment(Integer trackingId, String customerUsername,
                   Double amount, String method, String timestamp) {
        this.trackingId       = trackingId;
        this.customerUsername = customerUsername;
        this.amount           = amount;
        this.method           = method;
        this.status           = "PAID";
        this.timestamp        = timestamp;
    }

    // ── Getters ──────────────────────────────────────────────
    public Integer getPaymentId()        { return paymentId; }
    public Integer getTrackingId()       { return trackingId; }
    public String  getCustomerUsername() { return customerUsername; }
    public Double  getAmount()           { return amount; }
    public String  getMethod()           { return method; }
    public String  getStatus()           { return status; }
    public String  getTimestamp()        { return timestamp; }

    // ── Setters ──────────────────────────────────────────────
    public void setPaymentId(Integer paymentId)              { this.paymentId = paymentId; }
    public void setTrackingId(Integer trackingId)            { this.trackingId = trackingId; }
    public void setCustomerUsername(String customerUsername) { this.customerUsername = customerUsername; }
    public void setAmount(Double amount)                     { this.amount = amount; }
    public void setMethod(String method)                     { this.method = method; }
    public void setStatus(String status)                     { this.status = status; }
    public void setTimestamp(String timestamp)               { this.timestamp = timestamp; }
}

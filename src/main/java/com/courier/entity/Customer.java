package com.courier.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Integer customerId;

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address")
    private String address;

    @NotNull
    @Column(name = "phone", nullable = false)
    private Long phone;

    @NotBlank
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    public Customer() {}

    public Customer(String name, String address, Long phone, String username) {
        this.name     = name;
        this.address  = address;
        this.phone    = phone;
        this.username = username;
    }

    public Integer getCustomerId() { return customerId; }
    public String  getName()       { return name; }
    public String  getAddress()    { return address; }
    public Long    getPhone()      { return phone; }
    public String  getUsername()   { return username; }

    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public void setName(String name)              { this.name = name; }
    public void setAddress(String address)        { this.address = address; }
    public void setPhone(Long phone)              { this.phone = phone; }
    public void setUsername(String username)      { this.username = username; }
}

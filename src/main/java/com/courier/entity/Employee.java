package com.courier.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_id")
    private Integer empId;

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

    @NotNull
    @Column(name = "pincode", nullable = false)
    private Integer pincode;

    @Column(name = "availability", nullable = false)
    private String availability = "FREE";   // FREE | BUSY | ON_LEAVE

    public Employee() {}

    public Employee(String name, String address, Long phone,
                    String username, Integer pincode) {
        this.name     = name;
        this.address  = address;
        this.phone    = phone;
        this.username = username;
        this.pincode  = pincode;
        this.availability = "FREE";
    }

    // ── Original business methods preserved exactly ──────────
    public void markBusy() { this.availability = "BUSY"; }
    public void markFree() { this.availability = "FREE"; }

    // ── Getters ──────────────────────────────────────────────
    public Integer getEmpId()        { return empId; }
    public String  getName()         { return name; }
    public String  getAddress()      { return address; }
    public Long    getPhone()        { return phone; }
    public String  getUsername()     { return username; }
    public Integer getPincode()      { return pincode; }
    public String  getAvailability() { return availability; }

    // ── Setters ──────────────────────────────────────────────
    public void setEmpId(Integer empId)             { this.empId = empId; }
    public void setName(String name)                { this.name = name; }
    public void setAddress(String address)          { this.address = address; }
    public void setPhone(Long phone)                { this.phone = phone; }
    public void setUsername(String username)        { this.username = username; }
    public void setPincode(Integer pincode)         { this.pincode = pincode; }
    public void setAvailability(String availability){ this.availability = availability; }
}

package com.courier.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "user_accounts")
public class UserAccount {

    @Id
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotBlank
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank
    @Column(name = "role", nullable = false)
    private String role;  // ADMIN | EMPLOYEE | CUSTOMER

    public UserAccount() {}

    public UserAccount(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role     = role;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole()     { return role; }

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role)         { this.role = role; }
}

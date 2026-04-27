package com.courier.controller;

import com.courier.dto.LoginRequestDto;
import com.courier.dto.RegisterRequestDto;
import com.courier.entity.Customer;
import com.courier.entity.UserAccount;
import com.courier.service.AuthService;
import com.courier.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AuthController
 * POST /api/auth/login    — login (admin / employee / customer)
 * POST /api/auth/register — register new customer
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService     authService;
    private final CustomerService customerService;

    public AuthController(AuthService authService, CustomerService customerService) {
        this.authService     = authService;
        this.customerService = customerService;
    }

    /**
     * POST /api/auth/login
     * Body: { "username": "admin", "password": "admin123" }
     * Returns: { "username", "role" }
     * Throws 401 InvalidLoginException if credentials are wrong.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequestDto req) {
        UserAccount acc = authService.login(req.getUsername(), req.getPassword());
        return ResponseEntity.ok(Map.of(
                "username", acc.getUsername(),
                "role",     acc.getRole(),
                "message",  "Login successful"
        ));
    }

    /**
     * POST /api/auth/register
     * Body: { "username", "password", "name", "address", "phone" }
     * Throws 409 DuplicateUsernameException if username taken.
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequestDto req) {
        Customer c = customerService.register(req);
        return ResponseEntity.ok(Map.of(
                "customerId", c.getCustomerId(),
                "username",   c.getUsername(),
                "name",       c.getName(),
                "message",    "Registration successful! Please login."
        ));
    }
}

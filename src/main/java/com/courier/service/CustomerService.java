package com.courier.service;

import com.courier.dto.RegisterRequestDto;
import com.courier.entity.Customer;
import com.courier.exception.ResourceNotFoundException;
import com.courier.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepo;
    private final AuthService        authService;

    public CustomerService(CustomerRepository customerRepo, AuthService authService) {
        this.customerRepo = customerRepo;
        this.authService  = authService;
    }

    public List<Customer> getAllCustomers() {
        return customerRepo.findAll();
    }

    public Customer getByUsername(String username) {
        return customerRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + username));
    }

    /**
     * Original registerCustomer() logic preserved:
     * signUpCustomer() throws DuplicateUsernameException if taken.
     */
    @Transactional
    public Customer register(RegisterRequestDto req) {
        authService.signUpCustomer(req.getUsername(), req.getPassword());
        Customer c = new Customer(req.getName(), req.getAddress(), req.getPhone(), req.getUsername());
        return customerRepo.save(c);
    }
}

package com.courier.service;

import com.courier.entity.Payment;
import com.courier.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository payRepo;
    public PaymentService(PaymentRepository payRepo) { this.payRepo = payRepo; }
    public List<Payment> getAllPayments()                       { return payRepo.findAll(); }
    public List<Payment> getByCustomerUsername(String username){ return payRepo.findByCustomerUsername(username); }
}

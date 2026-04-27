package com.courier.repository;

import com.courier.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByCustomerUsername(String customerUsername);
    Optional<Payment> findByTrackingId(Integer trackingId);
}

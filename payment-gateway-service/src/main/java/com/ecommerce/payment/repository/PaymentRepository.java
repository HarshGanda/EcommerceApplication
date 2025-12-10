package com.ecommerce.payment.repository;

import com.ecommerce.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentId(String paymentId);
    List<Payment> findByUserId(Long userId);
    List<Payment> findByOrderId(Long orderId);
}


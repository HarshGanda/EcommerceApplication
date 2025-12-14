package com.ecommerce.payment.controller;

import com.ecommerce.payment.dto.PaymentRequestDTO;
import com.ecommerce.payment.dto.PaymentResponseDTO;
import com.ecommerce.payment.service.IPaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private IPaymentService paymentService;

    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponseDTO> initiatePayment(@Valid @RequestBody PaymentRequestDTO request) {
        PaymentResponseDTO response = paymentService.initiatePayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{paymentId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentStatus(@PathVariable String paymentId) {
        PaymentResponseDTO response = paymentService.getPaymentStatus(paymentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByUserId(@PathVariable Long userId) {
        List<PaymentResponseDTO> payments = paymentService.getPaymentsByUserId(userId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByOrderId(@PathVariable Long orderId) {
        List<PaymentResponseDTO> payments = paymentService.getPaymentsByOrderId(orderId);
        return ResponseEntity.ok(payments);
    }
}
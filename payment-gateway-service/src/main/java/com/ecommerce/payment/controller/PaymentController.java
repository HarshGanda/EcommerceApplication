package com.ecommerce.payment.controller;

import com.ecommerce.payment.dto.PaymentRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @PostMapping("/initiate")
    public Map<String, String> initiatePayment(@RequestBody PaymentRequestDTO request) {
        // Implementation for payment initiation
        Map<String, String> response = new HashMap<>();
        response.put("paymentId", "pay_" + System.currentTimeMillis());
        response.put("status", "initiated");
        return response;
    }

    @GetMapping("/status/{id}")
    public Map<String, String> getPaymentStatus(@PathVariable String id) {
        Map<String, String> response = new HashMap<>();
        response.put("paymentId", id);
        response.put("status", "success");
        return response;
    }
}
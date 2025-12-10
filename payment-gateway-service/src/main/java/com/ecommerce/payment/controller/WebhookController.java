package com.ecommerce.payment.controller;

import com.ecommerce.payment.dto.PaymentResponseDTO;
import com.ecommerce.payment.service.IPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    @Autowired
    private IPaymentService paymentService;

    @PostMapping("/razorpay")
    public ResponseEntity<PaymentResponseDTO> razorpayWebhook(@RequestBody String payload) {
        PaymentResponseDTO response = paymentService.processRazorpayWebhook(payload);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/stripe")
    public ResponseEntity<PaymentResponseDTO> stripeWebhook(@RequestBody String payload) {
        PaymentResponseDTO response = paymentService.processStripeWebhook(payload);
        return ResponseEntity.ok(response);
    }
}
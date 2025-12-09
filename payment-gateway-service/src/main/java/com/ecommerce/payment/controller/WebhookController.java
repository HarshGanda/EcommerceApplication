package com.ecommerce.payment.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    @PostMapping("/razorpay")
    public void razorpayWebhook(@RequestBody String payload) {
        // Verify signature and process webhook
        System.out.println("Razorpay webhook received: " + payload);
    }

    @PostMapping("/stripe")
    public void stripeWebhook(@RequestBody String payload) {
        // Verify signature and process webhook
        System.out.println("Stripe webhook received: " + payload);
    }
}
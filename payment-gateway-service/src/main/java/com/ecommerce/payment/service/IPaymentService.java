package com.ecommerce.payment.service;

import com.ecommerce.payment.dto.PaymentRequestDTO;
import com.ecommerce.payment.dto.PaymentResponseDTO;

public interface IPaymentService {
    PaymentResponseDTO initiatePayment(PaymentRequestDTO request);
    PaymentResponseDTO getPaymentStatus(String paymentId);
    PaymentResponseDTO processRazorpayWebhook(String payload);
    PaymentResponseDTO processStripeWebhook(String payload);
}



package com.ecommerce.payment.service;

import com.ecommerce.payment.dto.PaymentRequestDTO;
import com.ecommerce.payment.dto.PaymentResponseDTO;

import java.util.List;

public interface IPaymentService {
    PaymentResponseDTO initiatePayment(PaymentRequestDTO request);
    PaymentResponseDTO getPaymentStatus(String paymentId);
    PaymentResponseDTO processRazorpayWebhook(String payload);
    PaymentResponseDTO processStripeWebhook(String payload);
    List<PaymentResponseDTO> getPaymentsByUserId(Long userId);
    List<PaymentResponseDTO> getPaymentsByOrderId(Long orderId);
}



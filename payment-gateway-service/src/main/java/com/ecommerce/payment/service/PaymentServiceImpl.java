package com.ecommerce.payment.service;

import com.ecommerce.payment.dto.PaymentRequestDTO;
import com.ecommerce.payment.dto.PaymentResponseDTO;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.PaymentMethod;
import com.ecommerce.payment.entity.PaymentStatus;
import com.ecommerce.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements IPaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Value("${razorpay.key.id:}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret:}")
    private String razorpayKeySecret;

    @Value("${stripe.api.key:}")
    private String stripeApiKey;

    @Override
    public PaymentResponseDTO initiatePayment(PaymentRequestDTO request) {
        // Create payment record
        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setUserId(request.getUserId());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency() != null ? request.getCurrency() : "INR");
        payment.setStatus(PaymentStatus.INITIATED);

        // Set payment method
        if ("razorpay".equalsIgnoreCase(request.getMethod())) {
            payment.setMethod(PaymentMethod.RAZORPAY);
        } else if ("stripe".equalsIgnoreCase(request.getMethod())) {
            payment.setMethod(PaymentMethod.STRIPE);
        } else {
            payment.setMethod(PaymentMethod.CREDIT_CARD);
        }

        // Generate payment ID
        payment.setPaymentId("PAY_" + System.currentTimeMillis());

        Payment savedPayment = paymentRepository.save(payment);

        // Create response
        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setPaymentId(savedPayment.getPaymentId());
        response.setStatus(savedPayment.getStatus().toString());
        response.setAmount(savedPayment.getAmount());
        response.setCurrency(savedPayment.getCurrency());
        response.setMessage("Payment initiated successfully");

        return response;
    }

    @Override
    public PaymentResponseDTO getPaymentStatus(String paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setPaymentId(payment.getPaymentId());
        response.setStatus(payment.getStatus().toString());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());
        response.setMessage("Payment status retrieved successfully");

        return response;
    }

    @Override
    public PaymentResponseDTO processRazorpayWebhook(String payload) {
        // In real implementation, validate webhook signature
        // For now, just return a mock response
        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setStatus("SUCCESS");
        response.setMessage("Razorpay webhook processed");
        return response;
    }

    @Override
    public PaymentResponseDTO processStripeWebhook(String payload) {
        // In real implementation, validate webhook signature
        // For now, just return a mock response
        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setStatus("SUCCESS");
        response.setMessage("Stripe webhook processed");
        return response;
    }
}


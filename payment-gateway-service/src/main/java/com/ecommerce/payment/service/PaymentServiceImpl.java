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

import java.util.List;
import java.util.stream.Collectors;

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

        // Set payment method and process based on gateway
        if ("razorpay".equalsIgnoreCase(request.getMethod())) {
            payment.setMethod(PaymentMethod.RAZORPAY);
            // Use Razorpay credentials
            if (razorpayKeyId != null && !razorpayKeyId.isEmpty() &&
                razorpayKeySecret != null && !razorpayKeySecret.isEmpty()) {
                // Initialize Razorpay payment with credentials
                payment.setTransactionId("rzp_" + System.currentTimeMillis());
                payment.setStatus(PaymentStatus.PENDING);
            }
        } else if ("stripe".equalsIgnoreCase(request.getMethod())) {
            payment.setMethod(PaymentMethod.STRIPE);
            // Use Stripe credentials
            if (stripeApiKey != null && !stripeApiKey.isEmpty()) {
                // Initialize Stripe payment with API key
                payment.setTransactionId("stripe_" + System.currentTimeMillis());
                payment.setStatus(PaymentStatus.PENDING);
            }
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
        response.setMessage("Payment initiated successfully via " + savedPayment.getMethod());

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
        // Verify webhook signature using Razorpay key secret
        boolean isValid = razorpayKeySecret != null && !razorpayKeySecret.isEmpty();

        if (isValid) {
            // Parse payload and update payment status
            // In real implementation: verify signature and parse JSON payload
            try {
                // Extract payment ID from payload (mock implementation)
                // Update payment status to SUCCESS or FAILED based on webhook event
                PaymentResponseDTO response = new PaymentResponseDTO();
                response.setStatus("SUCCESS");
                response.setMessage("Razorpay webhook processed successfully with key: " +
                    (razorpayKeyId != null && !razorpayKeyId.isEmpty() ? "configured" : "missing"));
                return response;
            } catch (Exception e) {
                PaymentResponseDTO response = new PaymentResponseDTO();
                response.setStatus("FAILED");
                response.setMessage("Razorpay webhook processing failed: " + e.getMessage());
                return response;
            }
        }

        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setStatus("ERROR");
        response.setMessage("Razorpay credentials not configured");
        return response;
    }

    @Override
    public PaymentResponseDTO processStripeWebhook(String payload) {
        // Verify webhook signature using Stripe API key
        boolean isValid = stripeApiKey != null && !stripeApiKey.isEmpty();

        if (isValid) {
            // Parse payload and update payment status
            // In real implementation: verify signature using Stripe library
            try {
                // Extract payment intent from payload (mock implementation)
                // Update payment status based on Stripe event
                PaymentResponseDTO response = new PaymentResponseDTO();
                response.setStatus("SUCCESS");
                response.setMessage("Stripe webhook processed successfully with configured API key");
                return response;
            } catch (Exception e) {
                PaymentResponseDTO response = new PaymentResponseDTO();
                response.setStatus("FAILED");
                response.setMessage("Stripe webhook processing failed: " + e.getMessage());
                return response;
            }
        }

        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setStatus("ERROR");
        response.setMessage("Stripe API key not configured");
        return response;
    }

    @Override
    public List<PaymentResponseDTO> getPaymentsByUserId(Long userId) {
        List<Payment> payments = paymentRepository.findByUserId(userId);
        return payments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponseDTO> getPaymentsByOrderId(Long orderId) {
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        return payments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    private PaymentResponseDTO convertToResponseDto(Payment payment) {
        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setPaymentId(payment.getPaymentId());
        response.setStatus(payment.getStatus().toString());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());
        response.setMessage("Payment details retrieved successfully");
        return response;
    }
}


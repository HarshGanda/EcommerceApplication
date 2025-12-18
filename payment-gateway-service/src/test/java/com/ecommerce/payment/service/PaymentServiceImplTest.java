package com.ecommerce.payment.service;

import com.ecommerce.payment.dto.PaymentRequestDTO;
import com.ecommerce.payment.dto.PaymentResponseDTO;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.PaymentMethod;
import com.ecommerce.payment.entity.PaymentStatus;
import com.ecommerce.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment testPayment;
    private PaymentRequestDTO testRequest;

    @BeforeEach
    void setUp() {
        testPayment = new Payment();
        testPayment.setId(1L);
        testPayment.setPaymentId("PAY_123");
        testPayment.setOrderId(1L);
        testPayment.setUserId(1L);
        testPayment.setAmount(1000.0);
        testPayment.setCurrency("INR");
        testPayment.setStatus(PaymentStatus.PENDING);
        testPayment.setMethod(PaymentMethod.RAZORPAY);
        testPayment.setTransactionId("rzp_123");

        testRequest = new PaymentRequestDTO();
        testRequest.setOrderId(1L);
        testRequest.setUserId(1L);
        testRequest.setAmount(1000.0);
        testRequest.setCurrency("INR");
        testRequest.setMethod("razorpay");
    }

    @Test
    void testInitiatePayment() {
        // Test: Razorpay payment, Stripe payment, Credit card payment, default currency
        ReflectionTestUtils.setField(paymentService, "razorpayKeyId", "rzp_test_key");
        ReflectionTestUtils.setField(paymentService, "razorpayKeySecret", "rzp_test_secret");
        ReflectionTestUtils.setField(paymentService, "stripeApiKey", "sk_test_stripe");

        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        PaymentResponseDTO result = paymentService.initiatePayment(testRequest);
        assertNotNull(result);
        assertNotNull(result.getPaymentId());
        assertTrue(result.getPaymentId().startsWith("PAY_"));
        assertEquals("PENDING", result.getStatus());
        assertNotNull(result.getMessage());

        testRequest.setMethod("stripe");
        result = paymentService.initiatePayment(testRequest);
        assertNotNull(result);
        assertEquals("PENDING", result.getStatus());
        assertNotNull(result.getMessage());

        testRequest.setMethod("credit_card");
        result = paymentService.initiatePayment(testRequest);
        assertNotNull(result);
        assertEquals("INITIATED", result.getStatus());

        testRequest.setCurrency(null);
        result = paymentService.initiatePayment(testRequest);
        assertEquals("INR", result.getCurrency());
    }

    @Test
    void testGetPaymentStatus() {
        // Test: Get payment status (found/not found)
        when(paymentRepository.findByPaymentId("PAY_123")).thenReturn(Optional.of(testPayment));

        PaymentResponseDTO result = paymentService.getPaymentStatus("PAY_123");
        assertNotNull(result);
        assertEquals("PAY_123", result.getPaymentId());
        assertEquals("PENDING", result.getStatus());
        assertEquals(1000.0, result.getAmount());

        when(paymentRepository.findByPaymentId("INVALID")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> paymentService.getPaymentStatus("INVALID"));
    }

    @Test
    void testProcessWebhooks() {
        // Test: Razorpay webhook (success/failure/no credentials), Stripe webhook
        ReflectionTestUtils.setField(paymentService, "razorpayKeyId", "rzp_test_key");
        ReflectionTestUtils.setField(paymentService, "razorpayKeySecret", "rzp_test_secret");
        ReflectionTestUtils.setField(paymentService, "stripeApiKey", "sk_test_stripe");

        PaymentResponseDTO result = paymentService.processRazorpayWebhook("{\"event\":\"payment.captured\"}");
        assertNotNull(result);
        assertEquals("SUCCESS", result.getStatus());
        assertTrue(result.getMessage().contains("configured"));

        ReflectionTestUtils.setField(paymentService, "razorpayKeySecret", "");
        result = paymentService.processRazorpayWebhook("{\"event\":\"payment.captured\"}");
        assertEquals("ERROR", result.getStatus());
        assertTrue(result.getMessage().contains("not configured"));

        ReflectionTestUtils.setField(paymentService, "stripeApiKey", "sk_test_stripe");
        result = paymentService.processStripeWebhook("{\"type\":\"payment_intent.succeeded\"}");
        assertNotNull(result);
        assertEquals("SUCCESS", result.getStatus());

        ReflectionTestUtils.setField(paymentService, "stripeApiKey", "");
        result = paymentService.processStripeWebhook("{\"type\":\"payment_intent.succeeded\"}");
        assertEquals("ERROR", result.getStatus());
    }

    @Test
    void testGetPaymentsByUserAndOrder() {
        // Test: Get payments by user ID, get payments by order ID
        List<Payment> payments = Arrays.asList(testPayment);

        when(paymentRepository.findByUserId(1L)).thenReturn(payments);
        List<PaymentResponseDTO> userPayments = paymentService.getPaymentsByUserId(1L);
        assertEquals(1, userPayments.size());
        assertEquals("PAY_123", userPayments.get(0).getPaymentId());

        when(paymentRepository.findByOrderId(1L)).thenReturn(payments);
        List<PaymentResponseDTO> orderPayments = paymentService.getPaymentsByOrderId(1L);
        assertEquals(1, orderPayments.size());
        assertEquals(1000.0, orderPayments.get(0).getAmount());
    }
}


package com.ecommerce.payment.integration;

import com.ecommerce.payment.dto.PaymentRequestDTO;
import com.ecommerce.payment.dto.PaymentResponseDTO;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.repository.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PaymentServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
    }

    @Test
    void testCompletePaymentFlow() throws Exception {
        // Test: Initiate payment, check status, query by user/order

        // Initiate payment
        PaymentRequestDTO request = new PaymentRequestDTO();
        request.setOrderId(1L);
        request.setUserId(1L);
        request.setAmount(1000.0);
        request.setCurrency("INR");
        request.setMethod("razorpay");

        String response = mockMvc.perform(post("/payments/initiate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").exists())
                .andExpect(jsonPath("$.status").isNotEmpty())
                .andExpect(jsonPath("$.amount").value(1000.0))
                .andExpect(jsonPath("$.currency").value("INR"))
                .andReturn().getResponse().getContentAsString();

        PaymentResponseDTO paymentResponse = objectMapper.readValue(response, PaymentResponseDTO.class);
        String paymentId = paymentResponse.getPaymentId();

        // Get payment status
        mockMvc.perform(get("/payments/status/" + paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(paymentId))
                .andExpect(jsonPath("$.amount").value(1000.0));

        // Get payments by user ID
        mockMvc.perform(get("/payments/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].paymentId").value(paymentId));

        // Get payments by order ID
        mockMvc.perform(get("/payments/order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].paymentId").value(paymentId));
    }

    @Test
    void testMultiplePaymentMethods() throws Exception {
        // Test: Different payment methods
        String[] methods = {"razorpay", "stripe", "credit_card"};

        for (int i = 0; i < methods.length; i++) {
            PaymentRequestDTO request = new PaymentRequestDTO();
            request.setOrderId((long) (i + 1));
            request.setUserId(1L);
            request.setAmount(500.0 * (i + 1));
            request.setCurrency("INR");
            request.setMethod(methods[i]);

            mockMvc.perform(post("/payments/initiate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.paymentId").exists());
        }

        // Verify all payments created
        mockMvc.perform(get("/payments/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void testWebhookProcessing() throws Exception {
        // Test: Process webhook events

        // Razorpay webhook
        String razorpayPayload = "{\"event\":\"payment.captured\",\"payload\":{\"payment\":{\"entity\":{\"id\":\"pay_123\"}}}}";

        mockMvc.perform(post("/webhook/razorpay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(razorpayPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists());

        // Stripe webhook
        String stripePayload = "{\"type\":\"payment_intent.succeeded\",\"data\":{\"object\":{\"id\":\"pi_123\"}}}";

        mockMvc.perform(post("/webhook/stripe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(stripePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists());
    }

    @Test
    void testPaymentWithDifferentCurrencies() throws Exception {
        // Test: Payments in different currencies
        String[] currencies = {"INR", "USD", "EUR"};

        for (String currency : currencies) {
            PaymentRequestDTO request = new PaymentRequestDTO();
            request.setOrderId(100L);
            request.setUserId(2L);
            request.setAmount(1000.0);
            request.setCurrency(currency);
            request.setMethod("razorpay");

            mockMvc.perform(post("/payments/initiate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.currency").value(currency));
        }
    }

    @Test
    void testGetPaymentByNonExistentId() throws Exception {
        // Test: Get non-existent payment returns error
        mockMvc.perform(get("/payments/status/INVALID_ID"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("payment-gateway-service"));
    }
}


package com.ecommerce.payment.controller;

import com.ecommerce.payment.dto.PaymentResponseDTO;
import com.ecommerce.payment.service.IPaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class WebhookControllerTest {

    @Mock
    private IPaymentService paymentService;

    @InjectMocks
    private WebhookController webhookController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(webhookController).build();
    }

    @Test
    void testWebhooks() throws Exception {
        // Test: Razorpay webhook, Stripe webhook
        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setStatus("SUCCESS");
        response.setMessage("Webhook processed");

        when(paymentService.processRazorpayWebhook(anyString())).thenReturn(response);

        mockMvc.perform(post("/webhook/razorpay")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"event\":\"payment.captured\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        when(paymentService.processStripeWebhook(anyString())).thenReturn(response);

        mockMvc.perform(post("/webhook/stripe")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"payment_intent.succeeded\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }
}


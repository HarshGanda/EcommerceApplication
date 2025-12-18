package com.ecommerce.payment.controller;

import com.ecommerce.payment.dto.PaymentRequestDTO;
import com.ecommerce.payment.dto.PaymentResponseDTO;
import com.ecommerce.payment.service.IPaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private IPaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private PaymentRequestDTO testRequest;
    private PaymentResponseDTO testResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
        objectMapper = new ObjectMapper();

        testRequest = new PaymentRequestDTO();
        testRequest.setOrderId(1L);
        testRequest.setUserId(1L);
        testRequest.setAmount(1000.0);
        testRequest.setCurrency("INR");
        testRequest.setMethod("razorpay");

        testResponse = new PaymentResponseDTO();
        testResponse.setPaymentId("PAY_123");
        testResponse.setStatus("PENDING");
        testResponse.setAmount(1000.0);
        testResponse.setCurrency("INR");
        testResponse.setMessage("Payment initiated successfully");
    }

    @Test
    void testPaymentOperations() throws Exception {
        // Test: Initiate payment, get payment status, get payments by user/order
        when(paymentService.initiatePayment(any(PaymentRequestDTO.class))).thenReturn(testResponse);

        mockMvc.perform(post("/payments/initiate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value("PAY_123"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.amount").value(1000.0));

        when(paymentService.getPaymentStatus("PAY_123")).thenReturn(testResponse);

        mockMvc.perform(get("/payments/status/PAY_123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value("PAY_123"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        List<PaymentResponseDTO> payments = Arrays.asList(testResponse);
        when(paymentService.getPaymentsByUserId(1L)).thenReturn(payments);

        mockMvc.perform(get("/payments/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].paymentId").value("PAY_123"));

        when(paymentService.getPaymentsByOrderId(1L)).thenReturn(payments);

        mockMvc.perform(get("/payments/order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(1000.0));
    }
}


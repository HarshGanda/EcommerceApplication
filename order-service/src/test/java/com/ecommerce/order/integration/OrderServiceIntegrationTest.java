package com.ecommerce.order.integration;

import com.ecommerce.order.dto.OrderDto;
import com.ecommerce.order.dto.OrderItemDto;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.repository.OrderRepository;
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

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OrderServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    void testCompleteOrderLifecycle() throws Exception {
        // Test: Create order, get order, update status, cancel

        // Create order
        OrderDto orderDto = new OrderDto();
        orderDto.setUserId(1L);
        orderDto.setShippingAddress("123 Main St");
        orderDto.setTotalAmount(500.0);

        List<OrderItemDto> items = new ArrayList<>();
        OrderItemDto item1 = new OrderItemDto();
        item1.setProductId(101L);
        item1.setProductName("Product A");
        item1.setQuantity(2);
        item1.setPrice(150.0);
        items.add(item1);

        OrderItemDto item2 = new OrderItemDto();
        item2.setProductId(102L);
        item2.setProductName("Product B");
        item2.setQuantity(1);
        item2.setPrice(200.0);
        items.add(item2);

        orderDto.setItems(items);

        String response = mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.totalAmount").value(500.0))
                .andReturn().getResponse().getContentAsString();

        OrderDto createdOrder = objectMapper.readValue(response, OrderDto.class);
        Long orderId = createdOrder.getId();

        // Get order by ID
        mockMvc.perform(get("/orders/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.userId").value(1));

        // Get orders by user ID
        mockMvc.perform(get("/orders/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(orderId));

        // Update order status
        mockMvc.perform(put("/orders/" + orderId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"SHIPPED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));

        // Verify status updated
        mockMvc.perform(get("/orders/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));

        // Cancel order
        mockMvc.perform(delete("/orders/" + orderId))
                .andExpect(status().isNoContent());

        // Verify cancellation
        mockMvc.perform(get("/orders/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void testMultipleOrdersForSameUser() throws Exception {
        // Test: Create multiple orders for same user
        Long userId = 2L;

        for (int i = 1; i <= 3; i++) {
            OrderDto orderDto = new OrderDto();
            orderDto.setUserId(userId);
            orderDto.setShippingAddress("Address " + i);
            orderDto.setTotalAmount(100.0 * i);
            orderDto.setItems(new ArrayList<>());

            mockMvc.perform(post("/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(orderDto)))
                    .andExpect(status().isOk());
        }

        // Get all orders for user
        mockMvc.perform(get("/orders/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void testOrderStatusProgression() throws Exception {
        // Test: Progress order through different statuses
        OrderDto orderDto = new OrderDto();
        orderDto.setUserId(3L);
        orderDto.setShippingAddress("Test Address");
        orderDto.setTotalAmount(200.0);
        orderDto.setItems(new ArrayList<>());

        String response = mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        OrderDto created = objectMapper.readValue(response, OrderDto.class);
        Long orderId = created.getId();

        // PENDING -> PROCESSING
        mockMvc.perform(put("/orders/" + orderId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"PROCESSING\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PROCESSING"));

        // PROCESSING -> SHIPPED
        mockMvc.perform(put("/orders/" + orderId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"SHIPPED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));

        // SHIPPED -> DELIVERED
        mockMvc.perform(put("/orders/" + orderId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"DELIVERED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED"));
    }

    @Test
    void testGetNonExistentOrder() throws Exception {
        // Test: Get non-existent order returns 404
        mockMvc.perform(get("/orders/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("order-service"));
    }
}


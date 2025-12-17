package com.ecommerce.order.controller;

import com.ecommerce.order.dto.OrderDto;
import com.ecommerce.order.dto.OrderItemDto;
import com.ecommerce.order.service.IOrderService;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private IOrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private OrderDto testOrderDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        objectMapper = new ObjectMapper();

        testOrderDto = new OrderDto();
        testOrderDto.setId(1L);
        testOrderDto.setUserId(1L);
        testOrderDto.setTotalAmount(200.0);
        testOrderDto.setStatus("PENDING");
        testOrderDto.setShippingAddress("123 Main St");
        testOrderDto.setItems(new ArrayList<>());

        OrderItemDto item = new OrderItemDto();
        item.setProductId(1L);
        item.setProductName("Product 1");
        item.setQuantity(2);
        item.setPrice(100.0);
        testOrderDto.getItems().add(item);
    }

    @Test
    void testOrderOperations() throws Exception {
        // Test: Create order, get order by ID, get orders by user, update status, cancel order
        when(orderService.createOrder(any(OrderDto.class))).thenReturn(testOrderDto);

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrderDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.totalAmount").value(200.0))
                .andExpect(jsonPath("$.status").value("PENDING"));

        when(orderService.getOrderById(1L)).thenReturn(testOrderDto);

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1));

        List<OrderDto> orders = Arrays.asList(testOrderDto);
        when(orderService.getOrdersByUserId(1L)).thenReturn(orders);

        mockMvc.perform(get("/orders/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1));

        testOrderDto.setStatus("SHIPPED");
        when(orderService.updateOrderStatus(anyLong(), anyString())).thenReturn(testOrderDto);

        mockMvc.perform(put("/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"SHIPPED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));

        doNothing().when(orderService).cancelOrder(anyLong());

        mockMvc.perform(delete("/orders/1"))
                .andExpect(status().isNoContent());
    }
}


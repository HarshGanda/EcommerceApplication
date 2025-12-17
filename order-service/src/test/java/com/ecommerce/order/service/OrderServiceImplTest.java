package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderDto;
import com.ecommerce.order.dto.OrderItemDto;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private OrderDto testOrderDto;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUserId(1L);
        testOrder.setTotalAmount(200.0);
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setShippingAddress("123 Main St");
        testOrder.setItems(new ArrayList<>());

        OrderItem item = new OrderItem();
        item.setId(1L);
        item.setProductId(1L);
        item.setProductName("Product 1");
        item.setQuantity(2);
        item.setPrice(100.0);
        item.setOrder(testOrder);
        testOrder.getItems().add(item);

        testOrderDto = new OrderDto();
        testOrderDto.setUserId(1L);
        testOrderDto.setTotalAmount(200.0);
        testOrderDto.setShippingAddress("123 Main St");
        testOrderDto.setItems(new ArrayList<>());

        OrderItemDto itemDto = new OrderItemDto();
        itemDto.setProductId(1L);
        itemDto.setProductName("Product 1");
        itemDto.setQuantity(2);
        itemDto.setPrice(100.0);
        testOrderDto.getItems().add(itemDto);
    }

    @Test
    void testCreateOrder() {
        // Test: Create order with items, Kafka notification sent
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> {
            Order o = i.getArgument(0);
            o.setId(1L);
            return o;
        });

        OrderDto result = orderService.createOrder(testOrderDto);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(200.0, result.getTotalAmount());
        assertEquals("PENDING", result.getStatus());
        assertEquals(1, result.getItems().size());
        verify(kafkaTemplate, times(1)).send(eq("notification-events"), anyString());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testGetOrders() {
        // Test: Get order by ID (found/not found), get orders by user ID
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        OrderDto result = orderService.getOrderById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getUserId());
        assertEquals("PENDING", result.getStatus());

        when(orderRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> orderService.getOrderById(999L));

        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findByUserId(1L)).thenReturn(orders);

        List<OrderDto> userOrders = orderService.getOrdersByUserId(1L);
        assertEquals(1, userOrders.size());
        assertEquals(1L, userOrders.get(0).getUserId());
    }

    @Test
    void testUpdateAndCancelOrder() {
        // Test: Update order status, cancel order, order not found
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        OrderDto result = orderService.updateOrderStatus(1L, "SHIPPED");
        assertNotNull(result);
        assertEquals(OrderStatus.SHIPPED, testOrder.getStatus());

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        orderService.cancelOrder(1L);
        assertEquals(OrderStatus.CANCELLED, testOrder.getStatus());
        verify(orderRepository, atLeastOnce()).save(testOrder);

        when(orderRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> orderService.updateOrderStatus(999L, "SHIPPED"));
        assertThrows(RuntimeException.class, () -> orderService.cancelOrder(999L));
    }
}


package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderDto;
import java.util.List;

public interface IOrderService {
    OrderDto createOrder(OrderDto orderDto);
    OrderDto getOrderById(Long id);
    List<OrderDto> getOrdersByUserId(Long userId);
    OrderDto updateOrderStatus(Long id, String status);
    void cancelOrder(Long id);
}


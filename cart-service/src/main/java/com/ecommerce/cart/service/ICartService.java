package com.ecommerce.cart.service;

import com.ecommerce.cart.dto.AddToCartRequest;
import com.ecommerce.cart.dto.CartDto;

public interface ICartService {
    CartDto getCartByUserId(Long userId);
    CartDto addToCart(AddToCartRequest request);
    CartDto updateCartItem(Long userId, Long productId, Integer quantity);
    CartDto removeFromCart(Long userId, Long productId);
    void clearCart(Long userId);
}


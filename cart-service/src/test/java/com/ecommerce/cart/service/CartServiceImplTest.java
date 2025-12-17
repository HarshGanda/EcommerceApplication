package com.ecommerce.cart.service;

import com.ecommerce.cart.dto.AddToCartRequest;
import com.ecommerce.cart.dto.CartDto;
import com.ecommerce.cart.entity.Cart;
import com.ecommerce.cart.entity.CartItem;
import com.ecommerce.cart.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private CartServiceImpl cartService;

    private Cart testCart;
    private CartItem testItem;

    @BeforeEach
    void setUp() {
        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUserId(1L);
        testCart.setTotalAmount(100.0);
        testCart.setItems(new ArrayList<>());

        testItem = new CartItem();
        testItem.setId(1L);
        testItem.setProductId(1L);
        testItem.setProductName("Test Product");
        testItem.setQuantity(2);
        testItem.setPrice(50.0);
        testItem.setCart(testCart);

        testCart.getItems().add(testItem);
    }

    @Test
    void testGetCartAndAddToCart() {
        // Test: Get existing cart, create new cart, add new item, update existing item
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));

        CartDto result = cartService.getCartByUserId(1L);
        assertEquals(1L, result.getUserId());
        assertEquals(100.0, result.getTotalAmount());
        assertEquals(1, result.getItems().size());

        // Test create new cart
        when(cartRepository.findByUserId(2L)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArgument(0));

        CartDto newCart = cartService.getCartByUserId(2L);
        assertEquals(2L, newCart.getUserId());
        assertEquals(0.0, newCart.getTotalAmount());

        // Test add new item to cart
        AddToCartRequest request = new AddToCartRequest();
        request.setUserId(1L);
        request.setProductId(2L);
        request.setProductName("Product 2");
        request.setQuantity(3);
        request.setPrice(30.0);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArgument(0));

        CartDto addedCart = cartService.addToCart(request);
        assertNotNull(addedCart);

        // Test update existing item quantity
        request.setProductId(1L);
        request.setQuantity(5);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));

        CartDto updatedCart = cartService.addToCart(request);
        assertNotNull(updatedCart);
    }

    @Test
    void testUpdateAndRemoveCartItems() {
        // Test: Update item quantity, remove item with quantity 0, item not found
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArgument(0));

        CartDto result = cartService.updateCartItem(1L, 1L, 5);
        assertNotNull(result);

        // Test remove item by setting quantity to 0
        result = cartService.updateCartItem(1L, 1L, 0);
        assertNotNull(result);

        // Test item not found
        assertThrows(RuntimeException.class, () ->
            cartService.updateCartItem(1L, 999L, 1));

        // Test cart not found
        when(cartRepository.findByUserId(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () ->
            cartService.updateCartItem(999L, 1L, 1));
    }

    @Test
    void testRemoveFromCartAndClear() {
        // Test: Remove item from cart, clear cart, cart not found
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArgument(0));

        CartDto result = cartService.removeFromCart(1L, 1L);
        assertNotNull(result);

        // Test clear cart
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        cartService.clearCart(1L);
        verify(cartRepository, atLeastOnce()).save(any(Cart.class));

        // Test cart not found
        when(cartRepository.findByUserId(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> cartService.clearCart(999L));
    }

    @Test
    void testGetCartSummary() {
        // Test: Get summary from cache, calculate and cache summary, empty cart summary
        @SuppressWarnings("unchecked")
        ValueOperations<String, Object> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        Map<String, Object> cachedSummary = new HashMap<>();
        cachedSummary.put("userId", 1L);
        cachedSummary.put("totalItems", 1);
        cachedSummary.put("totalQuantity", 2);
        cachedSummary.put("totalAmount", 100.0);

        when(valueOperations.get("cart:summary:1")).thenReturn(cachedSummary);

        Map<String, Object> result = cartService.getCartSummary(1L);
        assertEquals(1L, result.get("userId"));
        assertEquals(1, result.get("totalItems"));

        // Test calculate new summary
        when(valueOperations.get("cart:summary:2")).thenReturn(null);
        when(cartRepository.findByUserId(2L)).thenReturn(Optional.of(testCart));

        result = cartService.getCartSummary(2L);
        assertNotNull(result);
        verify(valueOperations).set(eq("cart:summary:2"), any(), eq(5L), eq(TimeUnit.MINUTES));

        // Test empty cart summary
        when(valueOperations.get("cart:summary:3")).thenReturn(null);
        when(cartRepository.findByUserId(3L)).thenReturn(Optional.empty());

        result = cartService.getCartSummary(3L);
        assertEquals(0, result.get("totalItems"));
        assertEquals(0, result.get("totalQuantity"));
        assertEquals(0.0, result.get("totalAmount"));
    }
}


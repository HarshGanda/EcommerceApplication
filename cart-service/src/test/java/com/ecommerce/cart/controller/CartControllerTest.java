package com.ecommerce.cart.controller;

import com.ecommerce.cart.dto.AddToCartRequest;
import com.ecommerce.cart.dto.CartDto;
import com.ecommerce.cart.dto.CartItemDto;
import com.ecommerce.cart.service.ICartService;
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

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private ICartService cartService;

    @InjectMocks
    private CartController cartController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private CartDto testCart;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
        objectMapper = new ObjectMapper();

        testCart = new CartDto();
        testCart.setId(1L);
        testCart.setUserId(1L);
        testCart.setTotalAmount(100.0);
        testCart.setItems(new ArrayList<>());

        CartItemDto item = new CartItemDto();
        item.setId(1L);
        item.setProductId(1L);
        item.setProductName("Test Product");
        item.setQuantity(2);
        item.setPrice(50.0);
        testCart.getItems().add(item);
    }

    @Test
    void testCartOperations() throws Exception {
        // Test: Get cart, add to cart, update cart, remove from cart, clear cart
        when(cartService.getCartByUserId(1L)).thenReturn(testCart);

        mockMvc.perform(get("/cart/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.totalAmount").value(100.0))
                .andExpect(jsonPath("$.items[0].productId").value(1));

        // Test add to cart
        AddToCartRequest request = new AddToCartRequest();
        request.setUserId(1L);
        request.setProductId(2L);
        request.setProductName("Product 2");
        request.setQuantity(3);
        request.setPrice(30.0);

        when(cartService.addToCart(any(AddToCartRequest.class))).thenReturn(testCart);

        mockMvc.perform(post("/cart/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));

        // Test update cart item
        when(cartService.updateCartItem(anyLong(), anyLong(), anyInt())).thenReturn(testCart);

        mockMvc.perform(put("/cart/update")
                .param("userId", "1")
                .param("productId", "1")
                .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));

        // Test remove from cart
        when(cartService.removeFromCart(anyLong(), anyLong())).thenReturn(testCart);

        mockMvc.perform(delete("/cart/remove")
                .param("userId", "1")
                .param("productId", "1"))
                .andExpect(status().isOk());

        // Test clear cart
        doNothing().when(cartService).clearCart(anyLong());

        mockMvc.perform(delete("/cart/clear/1"))
                .andExpect(status().isNoContent());

        // Test clear cart
        doNothing().when(cartService).clearCart(anyLong());

        mockMvc.perform(delete("/cart/clear/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetCartSummary() throws Exception {
        // Test: Get cart summary with cached and calculated data
        Map<String, Object> summary = new HashMap<>();
        summary.put("userId", 1L);
        summary.put("itemCount", 1);
        summary.put("totalAmount", 100.0);
        summary.put("lastUpdated", System.currentTimeMillis());

        when(cartService.getCartSummary(1L)).thenReturn(summary);

        mockMvc.perform(get("/cart/summary/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.itemCount").value(1))
                .andExpect(jsonPath("$.totalAmount").value(100.0));
    }
}


package com.ecommerce.cart.integration;

import com.ecommerce.cart.dto.AddToCartRequest;
import com.ecommerce.cart.dto.CartDto;
import com.ecommerce.cart.entity.Cart;
import com.ecommerce.cart.repository.CartRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CartServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        cartRepository.deleteAll();
    }

    @Test
    void testCompleteCartFlow() throws Exception {
        // Test: Create cart, add items, update, remove, clear
        Long userId = 1L;

        // Add first item to cart
        AddToCartRequest request1 = new AddToCartRequest();
        request1.setUserId(userId);
        request1.setProductId(101L);
        request1.setProductName("Product 1");
        request1.setQuantity(2);
        request1.setPrice(50.0);

        mockMvc.perform(post("/cart/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].productId").value(101))
                .andExpect(jsonPath("$.totalAmount").value(100.0));

        // Add second item
        AddToCartRequest request2 = new AddToCartRequest();
        request2.setUserId(userId);
        request2.setProductId(102L);
        request2.setProductName("Product 2");
        request2.setQuantity(1);
        request2.setPrice(75.0);

        mockMvc.perform(post("/cart/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.totalAmount").value(175.0));

        // Get cart
        mockMvc.perform(get("/cart/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.items", hasSize(2)));

        // Update item quantity
        mockMvc.perform(put("/cart/update")
                .param("userId", userId.toString())
                .param("productId", "101")
                .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount").value(325.0));

        // Remove item
        mockMvc.perform(delete("/cart/remove")
                .param("userId", userId.toString())
                .param("productId", "102"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.totalAmount").value(250.0));

        // Clear cart
        mockMvc.perform(delete("/cart/clear/" + userId))
                .andExpect(status().isNoContent());

        // Verify cart is empty
        mockMvc.perform(get("/cart/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)))
                .andExpect(jsonPath("$.totalAmount").value(0.0));
    }

    @Test
    void testCartSummary() throws Exception {
        // Test: Cart summary calculation
        Long userId = 2L;

        AddToCartRequest request = new AddToCartRequest();
        request.setUserId(userId);
        request.setProductId(201L);
        request.setProductName("Test Product");
        request.setQuantity(3);
        request.setPrice(100.0);

        mockMvc.perform(post("/cart/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/cart/summary/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.totalQuantity").value(3))
                .andExpect(jsonPath("$.totalAmount").value(300.0));
    }

    @Test
    void testAddSameProductTwice() throws Exception {
        // Test: Adding same product twice updates quantity
        Long userId = 3L;

        AddToCartRequest request = new AddToCartRequest();
        request.setUserId(userId);
        request.setProductId(301L);
        request.setProductName("Product");
        request.setQuantity(2);
        request.setPrice(50.0);

        // Add first time
        mockMvc.perform(post("/cart/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].quantity").value(2));

        // Add same product again
        request.setQuantity(3);
        mockMvc.perform(post("/cart/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].quantity").value(5))
                .andExpect(jsonPath("$.totalAmount").value(250.0));
    }

    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("cart-service"));
    }
}


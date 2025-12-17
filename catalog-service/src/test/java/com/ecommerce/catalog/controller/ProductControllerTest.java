package com.ecommerce.catalog.controller;

import com.ecommerce.catalog.dto.CategoryDto;
import com.ecommerce.catalog.dto.ProductDto;
import com.ecommerce.catalog.entity.Category;
import com.ecommerce.catalog.entity.Product;
import com.ecommerce.catalog.service.IProductService;
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
class ProductControllerTest {

    @Mock
    private IProductService productService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Product testProduct;
    private ProductDto testProductDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Laptop");
        testProduct.setDescription("Gaming Laptop");
        testProduct.setPrice(1000.0);
        testProduct.setCategory(category);

        testProductDto = new ProductDto();
        testProductDto.setId(1L);
        testProductDto.setName("Laptop");
        testProductDto.setDescription("Gaming Laptop");
        testProductDto.setPrice(1000.0);
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Electronics");
        testProductDto.setCategory(categoryDto);
    }

    @Test
    void testProductCRUDOperations() throws Exception {
        // Test: Get all products, get by ID, create, update, delete
        List<Product> products = Arrays.asList(testProduct);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[0].price").value(1000.0));

        when(productService.getProductById(1L)).thenReturn(testProduct);
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"));

        when(productService.save(any(Product.class))).thenReturn(testProduct);
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProductDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"));

        when(productService.replaceProduct(anyLong(), any(Product.class))).thenReturn(testProduct);
        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProductDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"));

        doNothing().when(productService).deleteProduct(anyLong());
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());
    }
}


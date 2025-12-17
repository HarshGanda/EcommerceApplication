package com.ecommerce.catalog.controller;

import com.ecommerce.catalog.entity.Category;
import com.ecommerce.catalog.entity.Product;
import com.ecommerce.catalog.service.IProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SearchControllerTest {

    @Mock
    private IProductService productService;

    @InjectMocks
    private SearchController searchController;

    private MockMvc mockMvc;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(searchController).build();

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Laptop");
        testProduct.setDescription("Gaming Laptop");
        testProduct.setPrice(1000.0);
        testProduct.setCategory(category);
    }

    @Test
    void testSearchProducts() throws Exception {
        // Test: Search with all parameters, pagination, and sorting
        List<Product> products = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(products);

        when(productService.searchProducts(anyString(), anyDouble(), anyDouble(), anyLong(), any(Pageable.class)))
                .thenReturn(productPage);

        mockMvc.perform(get("/search/products")
                .param("name", "Laptop")
                .param("minPrice", "500")
                .param("maxPrice", "1500")
                .param("categoryId", "1")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "price")
                .param("sortDirection", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products[0].name").value("Laptop"))
                .andExpect(jsonPath("$.products[0].price").value(1000.0))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));

        verify(productService, times(1)).searchProducts(anyString(), anyDouble(), anyDouble(), anyLong(), any(Pageable.class));
    }

    @Test
    void testSearchProductsWithDefaultParams() throws Exception {
        // Test: Search with default parameters (no filters)
        List<Product> products = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(products);

        when(productService.searchProducts(isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(productPage);

        mockMvc.perform(get("/search/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products").isArray())
                .andExpect(jsonPath("$.currentPage").exists())
                .andExpect(jsonPath("$.totalItems").exists());

        verify(productService, times(1)).searchProducts(isNull(), isNull(), isNull(), isNull(), any(Pageable.class));
    }

    @Test
    void testSearchByCategory() throws Exception {
        // Test: Search products by category ID
        List<Product> products = Arrays.asList(testProduct);
        when(productService.getProductsByCategory(1L)).thenReturn(products);

        mockMvc.perform(get("/search/products/by-category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[0].category.id").value(1))
                .andExpect(jsonPath("$[0].category.name").value("Electronics"));

        verify(productService, times(1)).getProductsByCategory(1L);
    }

    @Test
    void testSearchByPriceRange() throws Exception {
        // Test: Search products by price range
        List<Product> products = Arrays.asList(testProduct);
        when(productService.getProductsByPriceRange(500.0, 1500.0)).thenReturn(products);

        mockMvc.perform(get("/search/products/by-price-range")
                .param("minPrice", "500.0")
                .param("maxPrice", "1500.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[0].price").value(1000.0));

        verify(productService, times(1)).getProductsByPriceRange(500.0, 1500.0);
    }

    @Test
    void testSearchProductsEmptyResults() throws Exception {
        // Test: Search with no results
        Page<Product> emptyPage = new PageImpl<>(Arrays.asList());
        when(productService.searchProducts(eq("NonExistent"), eq(10000.0), eq(20000.0), isNull(), any(Pageable.class)))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/search/products")
                .param("name", "NonExistent")
                .param("minPrice", "10000")
                .param("maxPrice", "20000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products").isEmpty())
                .andExpect(jsonPath("$.totalItems").value(0));

        verify(productService, times(1)).searchProducts(eq("NonExistent"), eq(10000.0), eq(20000.0), isNull(), any(Pageable.class));
    }
}


package com.ecommerce.catalog.service;

import com.ecommerce.catalog.entity.Category;
import com.ecommerce.catalog.entity.Product;
import com.ecommerce.catalog.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Electronics");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Laptop");
        testProduct.setDescription("Gaming Laptop");
        testProduct.setPrice(1000.0);
        testProduct.setStockQuantity(10);
        testProduct.setCategory(testCategory);
    }

    @Test
    void testGetAndSaveProducts() {
        // Test: Get all products, get by ID (found/not found), save product
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.getAllProducts();
        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());

        // Test get by ID
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        Product found = productService.getProductById(1L);
        assertNotNull(found);
        assertEquals("Laptop", found.getName());

        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        Product notFound = productService.getProductById(999L);
        assertNull(notFound);

        // Test save product
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        Product saved = productService.save(testProduct);
        assertNotNull(saved);
        assertEquals("Laptop", saved.getName());
    }

    @Test
    void testUpdateAndDeleteProducts() {
        // Test: Replace product, delete product
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product updated = productService.replaceProduct(1L, testProduct);
        assertNotNull(updated);
        assertEquals(1L, updated.getId());

        doNothing().when(productRepository).deleteById(anyLong());
        productService.deleteProduct(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void testSearchProducts() {
        // Test: Search by name, category, price range, combined filters, no filters
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(Arrays.asList(testProduct));

        // Search by name
        when(productRepository.findByNameContaining(anyString(), any(Pageable.class))).thenReturn(page);
        Page<Product> result = productService.searchProducts("Laptop", null, null, null, pageable);
        assertEquals(1, result.getTotalElements());

        // Search by category
        when(productRepository.findByCategory_Id(anyLong(), any(Pageable.class))).thenReturn(page);
        result = productService.searchProducts(null, null, null, 1L, pageable);
        assertEquals(1, result.getTotalElements());

        // Search by price range
        when(productRepository.findByPriceBetween(anyDouble(), anyDouble(), any(Pageable.class))).thenReturn(page);
        result = productService.searchProducts(null, 500.0, 1500.0, null, pageable);
        assertEquals(1, result.getTotalElements());

        // Combined search
        when(productRepository.findByNameContainingAndCategory_IdAndPriceBetween(
            anyString(), anyLong(), anyDouble(), anyDouble(), any(Pageable.class))).thenReturn(page);
        result = productService.searchProducts("Laptop", 500.0, 1500.0, 1L, pageable);
        assertEquals(1, result.getTotalElements());

        // No filters
        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);
        result = productService.searchProducts(null, null, null, null, pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetProductsByCategoryAndPriceRange() {
        // Test: Get products by category, get products by price range
        List<Product> products = Arrays.asList(testProduct);

        when(productRepository.findByCategory_Id(1L)).thenReturn(products);
        List<Product> result = productService.getProductsByCategory(1L);
        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());

        when(productRepository.findByPriceBetween(500.0, 1500.0)).thenReturn(products);
        result = productService.getProductsByPriceRange(500.0, 1500.0);
        assertEquals(1, result.size());
        assertEquals(1000.0, result.get(0).getPrice());
    }
}


package com.ecommerce.catalog.integration;

import com.ecommerce.catalog.dto.CategoryDto;
import com.ecommerce.catalog.dto.ProductDto;
import com.ecommerce.catalog.entity.Category;
import com.ecommerce.catalog.entity.Product;
import com.ecommerce.catalog.repository.CategoryRepository;
import com.ecommerce.catalog.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CatalogServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void testCompleteCatalogFlow() throws Exception {
        // Test: Create category, create products, search, update, delete

        // Create category
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Electronics");

        String categoryResponse = mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Electronics"))
                .andReturn().getResponse().getContentAsString();

        CategoryDto savedCategory = objectMapper.readValue(categoryResponse, CategoryDto.class);
        Long categoryId = savedCategory.getId();

        // Create product
        ProductDto productDto = new ProductDto();
        productDto.setName("Laptop");
        productDto.setDescription("Gaming Laptop");
        productDto.setPrice(1500.0);
        CategoryDto cat = new CategoryDto();
        cat.setId(categoryId);
        cat.setName("Electronics");
        productDto.setCategory(cat);

        String productResponse = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(1500.0))
                .andReturn().getResponse().getContentAsString();

        ProductDto savedProduct = objectMapper.readValue(productResponse, ProductDto.class);
        Long productId = savedProduct.getId();

        // Get all products
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Laptop"));

        // Get product by ID
        mockMvc.perform(get("/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"));

        // Update product
        productDto.setId(productId);
        productDto.setPrice(1400.0);
        mockMvc.perform(put("/products/" + productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(1400.0));

        // Search products
        mockMvc.perform(get("/search/products")
                .param("name", "Laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products", hasSize(1)))
                .andExpect(jsonPath("$.products[0].name").value("Laptop"));

        // Search by category
        mockMvc.perform(get("/search/products/by-category/" + categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // Delete product
        mockMvc.perform(delete("/products/" + productId))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testSearchByPriceRange() throws Exception {
        // Test: Search products by price range
        Category category = new Category();
        category.setName("Gadgets");
        category = categoryRepository.save(category);

        Product product1 = new Product();
        product1.setName("Cheap Item");
        product1.setDescription("Affordable");
        product1.setPrice(50.0);
        product1.setCategory(category);
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("Expensive Item");
        product2.setDescription("Premium");
        product2.setPrice(500.0);
        product2.setCategory(category);
        productRepository.save(product2);

        // Search for products in range 40-100
        mockMvc.perform(get("/search/products/by-price-range")
                .param("minPrice", "40.0")
                .param("maxPrice", "100.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Cheap Item"));
    }

    @Test
    void testPaginationAndSorting() throws Exception {
        // Test: Pagination and sorting in search
        Category category = new Category();
        category.setName("Books");
        category = categoryRepository.save(category);

        for (int i = 1; i <= 15; i++) {
            Product product = new Product();
            product.setName("Book " + i);
            product.setDescription("Description " + i);
            product.setPrice(10.0 * i);
            product.setCategory(category);
            productRepository.save(product);
        }

        // Test pagination
        mockMvc.perform(get("/search/products")
                .param("page", "0")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products", hasSize(5)))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.totalItems").value(15));

        // Test sorting by price descending
        mockMvc.perform(get("/search/products")
                .param("page", "0")
                .param("size", "5")
                .param("sortBy", "price")
                .param("sortDirection", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products[0].price").value(150.0));
    }

    @Test
    void testCategoryOperations() throws Exception {
        // Test: Complete category CRUD
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Clothing");

        // Create
        String response = mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        CategoryDto saved = objectMapper.readValue(response, CategoryDto.class);

        // Read
        mockMvc.perform(get("/categories/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Clothing"));

        // Update
        saved.setName("Apparel");
        mockMvc.perform(put("/categories/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(saved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Apparel"));

        // Delete
        mockMvc.perform(delete("/categories/" + saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("catalog-service"));
    }
}


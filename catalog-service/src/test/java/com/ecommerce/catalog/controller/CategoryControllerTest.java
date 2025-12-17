package com.ecommerce.catalog.controller;

import com.ecommerce.catalog.dto.CategoryDto;
import com.ecommerce.catalog.entity.Category;
import com.ecommerce.catalog.service.ICategoryService;
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
class CategoryControllerTest {

    @Mock
    private ICategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Category testCategory;
    private CategoryDto testCategoryDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
        objectMapper = new ObjectMapper();

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Electronics");

        testCategoryDto = new CategoryDto();
        testCategoryDto.setId(1L);
        testCategoryDto.setName("Electronics");
    }

    @Test
    void testGetAllCategories() throws Exception {
        // Test: Retrieve all categories
        List<Category> categories = Arrays.asList(testCategory);
        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Electronics"));

        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    void testGetCategoryById() throws Exception {
        // Test: Get category by ID (found and not found)
        when(categoryService.getCategoryById(1L)).thenReturn(testCategory);

        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Electronics"));

        when(categoryService.getCategoryById(999L)).thenReturn(null);

        mockMvc.perform(get("/categories/999"))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).getCategoryById(1L);
        verify(categoryService, times(1)).getCategoryById(999L);
    }

    @Test
    void testCreateCategory() throws Exception {
        // Test: Create new category
        when(categoryService.save(any(Category.class))).thenReturn(testCategory);

        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCategoryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Electronics"));

        verify(categoryService, times(1)).save(any(Category.class));
    }

    @Test
    void testUpdateCategory() throws Exception {
        // Test: Update existing category and non-existing category
        when(categoryService.updateCategory(eq(1L), any(Category.class))).thenReturn(testCategory);

        mockMvc.perform(put("/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCategoryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Electronics"));

        when(categoryService.updateCategory(eq(999L), any(Category.class))).thenReturn(null);

        mockMvc.perform(put("/categories/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCategoryDto)))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).updateCategory(eq(1L), any(Category.class));
        verify(categoryService, times(1)).updateCategory(eq(999L), any(Category.class));
    }

    @Test
    void testDeleteCategory() throws Exception {
        // Test: Delete category
        doNothing().when(categoryService).deleteCategory(anyLong());

        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteCategory(1L);
    }
}


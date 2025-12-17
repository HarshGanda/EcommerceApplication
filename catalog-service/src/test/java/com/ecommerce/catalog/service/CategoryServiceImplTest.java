package com.ecommerce.catalog.service;

import com.ecommerce.catalog.entity.Category;
import com.ecommerce.catalog.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Electronics");
    }

    @Test
    void testGetAndSaveCategories() {
        // Test: Get all categories, get by ID (found/not found), save category
        List<Category> categories = Arrays.asList(testCategory);
        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.getAllCategories();
        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).getName());

        // Test get by ID
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        Category found = categoryService.getCategoryById(1L);
        assertNotNull(found);
        assertEquals("Electronics", found.getName());

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());
        Category notFound = categoryService.getCategoryById(999L);
        assertNull(notFound);

        // Test save category
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);
        Category saved = categoryService.save(testCategory);
        assertNotNull(saved);
        assertEquals("Electronics", saved.getName());
    }

    @Test
    void testUpdateAndDeleteCategories() {
        // Test: Update existing category, update non-existing category, delete category
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        Category updated = categoryService.updateCategory(1L, testCategory);
        assertNotNull(updated);
        assertEquals(1L, updated.getId());
        assertEquals("Electronics", updated.getName());

        // Test update non-existing category
        when(categoryRepository.existsById(999L)).thenReturn(false);
        Category notUpdated = categoryService.updateCategory(999L, testCategory);
        assertNull(notUpdated);

        // Test delete category
        doNothing().when(categoryRepository).deleteById(anyLong());
        categoryService.deleteCategory(1L);
        verify(categoryRepository, times(1)).deleteById(1L);
    }
}


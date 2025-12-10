package com.ecommerce.catalog.service;

import com.ecommerce.catalog.entity.Category;
import java.util.List;

public interface ICategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(Long id);
    Category save(Category category);
    Category updateCategory(Long id, Category category);
    void deleteCategory(Long id);
}


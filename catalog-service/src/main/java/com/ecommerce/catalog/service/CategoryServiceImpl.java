package com.ecommerce.catalog.service;

import com.ecommerce.catalog.entity.Category;
import com.ecommerce.catalog.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    @Cacheable(value = "categories")
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Cacheable(value = "category", key = "#id")
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Override
    @CacheEvict(value = {"categories", "category"}, allEntries = true)
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    @CacheEvict(value = {"categories", "category"}, allEntries = true)
    public Category updateCategory(Long id, Category category) {
        if (categoryRepository.existsById(id)) {
            category.setId(id);
            return categoryRepository.save(category);
        }
        return null;
    }

    @Override
    @CacheEvict(value = {"categories", "category"}, allEntries = true)
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}


package com.ecommerce.catalog.service;

import com.ecommerce.catalog.entity.Product;
import com.ecommerce.catalog.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Cacheable(value = "products")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Cacheable(value = "product", key = "#id")
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public Product replaceProduct(Long id, Product product) {
        product.setId(id);
        return productRepository.save(product);
    }

    @Override
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public Page<Product> searchProducts(String name, Double minPrice, Double maxPrice, Long categoryId, Pageable pageable) {
        if (name != null && categoryId != null && minPrice != null && maxPrice != null) {
            return productRepository.findByNameContainingAndCategory_IdAndPriceBetween(name, categoryId, minPrice, maxPrice, pageable);
        } else if (name != null) {
            return productRepository.findByNameContaining(name, pageable);
        } else if (categoryId != null) {
            return productRepository.findByCategory_Id(categoryId, pageable);
        } else if (minPrice != null && maxPrice != null) {
            return productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
        } else {
            return productRepository.findAll(pageable);
        }
    }

    @Override
    @Cacheable(value = "productsByCategory", key = "#categoryId")
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategory_Id(categoryId);
    }

    @Override
    public List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }
}
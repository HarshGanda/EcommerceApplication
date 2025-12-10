package com.ecommerce.catalog.service;

import com.ecommerce.catalog.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductService {
    List<Product> getAllProducts();
    Product getProductById(Long id);
    Product save(Product product);
    Product replaceProduct(Long id, Product product);
    void deleteProduct(Long id);
    Page<Product> searchProducts(String name, Double minPrice, Double maxPrice, Long categoryId, Pageable pageable);
    List<Product> getProductsByCategory(Long categoryId);
    List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice);
}
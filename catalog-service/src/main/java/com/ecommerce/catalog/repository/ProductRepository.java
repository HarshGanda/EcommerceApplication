package com.ecommerce.catalog.repository;

import com.ecommerce.catalog.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByNameContaining(String name, Pageable pageable);
    Page<Product> findByCategory_Id(Long categoryId, Pageable pageable);
    Page<Product> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);
    Page<Product> findByNameContainingAndCategory_IdAndPriceBetween(String name, Long categoryId, Double minPrice, Double maxPrice, Pageable pageable);
    List<Product> findByCategory_Id(Long categoryId);
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
}


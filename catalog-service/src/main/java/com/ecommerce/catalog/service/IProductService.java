package com.ecommerce.catalog.service;

import com.ecommerce.catalog.entity.Product;
import java.util.List;

public interface IProductService {
    List<Product> getAllProducts();
    Product getProductById(Long id);
    Product save(Product product);
    Product replaceProduct(Long id, Product product);
    void deleteProduct(Long id);
}
package com.ecommerce.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private CategoryDto category;
    private Integer stockQuantity;
    private Boolean inStock;
    private String imageUrl;
    private String brand;
    private Double rating;
}
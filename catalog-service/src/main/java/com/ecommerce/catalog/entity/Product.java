package com.ecommerce.catalog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Double price;

    private Integer stockQuantity;

    private Boolean inStock;

    private String imageUrl;

    private String brand;

    private Double rating;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @PrePersist
    @PreUpdate
    protected void updateStockStatus() {
        this.inStock = this.stockQuantity != null && this.stockQuantity > 0;
    }
}


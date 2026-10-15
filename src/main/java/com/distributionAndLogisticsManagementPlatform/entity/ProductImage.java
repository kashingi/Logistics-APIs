package com.distributionAndLogisticsManagementPlatform.entity;

import jakarta.persistence.*;
import lombok.Data;

//Add your annotations here
@Data
@Entity
@Table(name = "product_images")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "product_image", nullable = false)
    private String productImage;

    private Integer sortOrder = 0;
}

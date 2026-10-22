package com.distributionAndLogisticsManagementPlatform.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

//Add your annotations here
@Data
@Entity
@Table(name = "stock_levels", uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "warehouse_id", "batch_id"}))
public class StockLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private Batch batch;                    // nullable for non-batch products

    private BigDecimal quantityOnHand;
}

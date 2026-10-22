package com.distributionAndLogisticsManagementPlatform.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

//Add your annotations here
@Data
@Entity
@Table(name = "stock_movements")
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String movementType;            // STOCK_IN, STOCK_OUT, TRANSFER_OUT, TRANSFER_IN, etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private Batch batch;

    private BigDecimal quantity;            // positive or negative

    private String referenceType;           // STOCK_TRANSFER, STOCK_IN, GRN, etc.
    private Long referenceId;

    private String createdBy;

    @CreationTimestamp
    private Instant createdAt;
}

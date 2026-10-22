package com.distributionAndLogisticsManagementPlatform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

//Add your annotations here
@Data
@Entity
@Table(name = "stock_transfer_lines")
public class StockTransferLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_transfer_id", nullable = false)
    @JsonIgnore
    private StockTransfer stockTransfer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private BigDecimal quantity;
    private String batchNo;
    private String notes;
}

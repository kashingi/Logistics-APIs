package com.distributionAndLogisticsManagementPlatform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

//Add your annotations here
@Data
@Entity
@Table(name = "sales_quotation_lines")
public class SalesQuotationLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_quotation_id", nullable = false)
    @JsonIgnore
    private SalesQuotation salesQuotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private BigDecimal quantity;
    private String uom;

    @Enumerated(EnumType.STRING)
    private String priceTier; //RETAIL or WHOLESALE

    private BigDecimal unitPrice;
    private BigDecimal discount = BigDecimal.ZERO;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private String fulfilment; // PICKUP DELIVERY
}

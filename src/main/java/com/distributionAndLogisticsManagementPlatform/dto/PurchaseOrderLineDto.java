package com.distributionAndLogisticsManagementPlatform.dto;

import lombok.Data;

import java.math.BigDecimal;

//Add your annotations here
@Data
public class PurchaseOrderLineDto {

    private Long productId;
    private Long warehouseId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal taxRate;
}

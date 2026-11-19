package com.distributionAndLogisticsManagementPlatform.dto;

import lombok.Data;

import java.math.BigDecimal;

//Add your annotations here
@Data
public class LowStockItemDto {

    private Long productId;
    private String productName;
    private String skuCode;
    private Long warehouseId;
    private String warehouseName;
    private BigDecimal quantityOnHand;
    private BigDecimal minimumQty; // optional threshold
    private String batchNo;
}

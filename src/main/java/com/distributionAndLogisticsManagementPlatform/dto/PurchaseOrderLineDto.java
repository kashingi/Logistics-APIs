package com.distributionAndLogisticsManagementPlatform.dto;

import lombok.Data;

//Add your annotations here
@Data
public class PurchaseOrderLineDto {

    private Long productId;
    private Long warehouseId;
    private Float quantity;
    private Float unitPrice;
    private Float discount;
    private String taxRate;
}

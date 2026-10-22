package com.distributionAndLogisticsManagementPlatform.dto;

import lombok.Data;

import java.math.BigDecimal;

//Add your annotations here
@Data
public class StockOutLineDto {

    private Long productId;
    private Long warehouseId;
    private BigDecimal quantity;
    private String batchNo;
}

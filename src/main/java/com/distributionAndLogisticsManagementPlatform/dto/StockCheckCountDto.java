package com.distributionAndLogisticsManagementPlatform.dto;

import lombok.Data;

import java.math.BigDecimal;

//Add your annotations here
@Data
public class StockCheckCountDto {

    private Long productId;
    private Long batchId;                   // optional
    private BigDecimal countedQuantity;
}

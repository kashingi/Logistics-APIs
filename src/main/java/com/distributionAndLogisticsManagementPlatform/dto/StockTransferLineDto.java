package com.distributionAndLogisticsManagementPlatform.dto;

import lombok.Data;

import java.math.BigDecimal;

//Add your annotations here
@Data
public class StockTransferLineDto {

    private Long productId;
    private BigDecimal quantity;
    private String batchNo;
    private String notes;
}

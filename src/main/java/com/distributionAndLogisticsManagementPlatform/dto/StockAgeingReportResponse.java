package com.distributionAndLogisticsManagementPlatform.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

//Add your annotations here
@Data
public class StockAgeingReportResponse {

    private String warehouse;
    private List<String> ageingBuckets;
    private List<StockAgeingResponse> rows;
    private BigDecimal totalStockOnHand;
}

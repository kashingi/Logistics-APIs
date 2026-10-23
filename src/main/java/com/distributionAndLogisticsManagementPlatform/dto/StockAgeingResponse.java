package com.distributionAndLogisticsManagementPlatform.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

//Add your annotations here
@Data
public class StockAgeingResponse {

    private String warehouseName;
    private String sku;
    private String barcode;
    private String productName;
    private String brand;
    private BigDecimal stockOnHand;
    private Map<String, BigDecimal> byBucket;   // e.g. {"0-15": 100, "16-30": 50, "31+": 20}
}

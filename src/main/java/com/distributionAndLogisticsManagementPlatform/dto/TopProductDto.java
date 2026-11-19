package com.distributionAndLogisticsManagementPlatform.dto;

import lombok.Data;

import java.math.BigDecimal;

//Add your annotations here
@Data
public class TopProductDto {

    private Long productId;
    private String productName;
    private String skuCode;
    private BigDecimal unitsSold;
    private String growthLabel;
}

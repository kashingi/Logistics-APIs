package com.distributionAndLogisticsManagementPlatform.dto;

import lombok.Data;

import java.math.BigDecimal;

//Add your annotations here
@Data
public class SalesQuotationLineDto {

    private Long productId;
    private BigDecimal quantity;
    private String priceTier;
    private BigDecimal discount;
    private String fulfilment;
}

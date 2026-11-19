package com.distributionAndLogisticsManagementPlatform.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

//Add your annotations here
@Data
public class StockCheckDto {

    private LocalDate checkDate;
    private String taskType;
    private Long warehouseId;
    private String description;
    private List<StockCheckCountDto> lines;
    //private List<Long> productIds;          // products to include in the check
    private String notes;
}

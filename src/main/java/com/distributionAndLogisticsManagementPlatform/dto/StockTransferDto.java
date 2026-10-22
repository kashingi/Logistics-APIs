package com.distributionAndLogisticsManagementPlatform.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

//Add your annotations here
@Data
public class StockTransferDto {

    private LocalDate date;
    private Long sourceWarehouseId;
    private Long destinationWarehouseId;
    private String transferStatus;
    private List<StockTransferLineDto> lines;
}

package com.distributionAndLogisticsManagementPlatform.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

//Add your annotations here
@Data
public class StockOutDto {

    private LocalDate date;
    private String stockOutReason;
    private String notes;
    private List<StockOutLineDto> lines;
}

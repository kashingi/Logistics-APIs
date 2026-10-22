package com.distributionAndLogisticsManagementPlatform.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

//Add your annotations here
@Data
public class PurchaseReturnDto {

    private Long supplierId;
    private LocalDate returnDate;
    private String remarks;
    private String status;
    private List<PurchaseReturnLineDto> lines;
}

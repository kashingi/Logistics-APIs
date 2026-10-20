package com.distributionAndLogisticsManagementPlatform.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

//Add your annotations here
@Data
public class PurchaseOrderDto {

    private Long supplierId;
    private LocalDate orderDate;
    private LocalDate expectedDeliveryDate;
    private List<PurchaseOrderLineDto> lines;
}

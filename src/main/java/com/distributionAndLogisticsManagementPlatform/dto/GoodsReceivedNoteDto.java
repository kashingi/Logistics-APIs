package com.distributionAndLogisticsManagementPlatform.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

//Add your annotations here
@Data
public class GoodsReceivedNoteDto {

    private Long supplierId;
    private Long purchaseOrderId;            // optional
    private LocalDate receivedDate;
    private String fulfilmentType;
    private List<GoodsReceivedNoteLineDto> lines;
}

package com.distributionAndLogisticsManagementPlatform.dto;

import lombok.Data;

import java.time.Instant;

//Add your annotations here
@Data
public class ActivityItemDto {

    private String type;        // ORDER, DELIVERY, STOCK, PO
    private String message;
    private String severity;    // INFO, WARNING, SUCCESS
    private Instant createdAt;
    private String timeAgo;     // "2m ago"
}

package com.distributionAndLogisticsManagementPlatform.dto;

import lombok.Data;

import java.util.List;

//Add your annotations here
@Data
public class DeliveryZoneDto {

    private String name;
    private List<String> locations;
    private String status;
}

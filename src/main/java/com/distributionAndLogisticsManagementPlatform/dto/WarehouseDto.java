package com.distributionAndLogisticsManagementPlatform.dto;

import lombok.Data;

//Add your annotations here
@Data
public class WarehouseDto {

    private String name;
    private String contactPerson;
    private String phone;
    private String email;
    private String street;
    private String city;
    private String postal;
    private String category;
    private boolean allowNegativeInventory;
    private String remark;
    private String status;
}

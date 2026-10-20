package com.distributionAndLogisticsManagementPlatform.dto;

import lombok.Data;

//Add your annotations here
@Data
public class SupplierDto {

    private String name;
    private String contactPerson;
    private String phone;
    private String email;
    private String paymentTerms;
    private String street;
    private String city;
    private String postal;
    private String status;
}

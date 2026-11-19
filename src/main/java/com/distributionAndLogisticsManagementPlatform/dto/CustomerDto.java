package com.distributionAndLogisticsManagementPlatform.dto;

import lombok.Data;

//Add your annotations here
@Data
public class CustomerDto {
    private String name;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String kraPin;
    private String street;
    private String city;
    private String postalCode;
    private String buildingLandmark;
    private String houseNo;
    private String contactPerson;
    private String contactPhone;
    private String status;
}

package com.distributionAndLogisticsManagementPlatform.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

//Add your annotations here
@Data
public class SalesQuotationDto {

    private Long customerId;                // existing customer
    private String customerName;            // or create new
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String kraPin;

    private String salespersonName;
    private String salespersonCode;

    private LocalDate date;
    private String notes;
    private String terms;

    private String buildingLandmark;
    private String street;
    private String houseNo;
    private String contactPerson;
    private String contactPhone;

    private List<SalesQuotationLineDto> lines;
}

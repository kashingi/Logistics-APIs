package com.distributionAndLogisticsManagementPlatform.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

//Add your annotations here
@Data
public class SalesOrderDto {

    private Long customerId;
    private String customerName;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String kraPin;

    private String salespersonName;
    private String salespersonCode;

    private Long sourceQuotationId;         // optional – if converting from quotation

    private LocalDate date;
    private String notes;
    private String terms;

    private String buildingLandmark;
    private String street;
    private String houseNo;
    private String contactPerson;
    private String contactPhone;

    private List<SalesOrderLineDto> lines;
}

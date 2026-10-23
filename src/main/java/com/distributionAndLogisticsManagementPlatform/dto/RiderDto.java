package com.distributionAndLogisticsManagementPlatform.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

//Add your annotations here
@Data
public class RiderDto {

    private String firstName;
    private String lastName;
    private String phone;
    private String nationalIdOrPassport;
    private LocalDate dateJoined;
    private String passportPhotoUrl;
    private String vehicleRegistrationNo;
    private String vehicleType;
    private BigDecimal carryingCapacityKg;
    private Integer contractDurationMonths;
    private Long currentZoneId;
    private String status;
}

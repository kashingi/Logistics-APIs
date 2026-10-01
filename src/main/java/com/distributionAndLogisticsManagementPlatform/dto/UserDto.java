package com.distributionAndLogisticsManagementPlatform.dto;

import lombok.Data;

//Add annotations here
@Data
public class UserDto {

    private String email;
    private String fullName;
    private String password;
    private String role;
    private String active;
}

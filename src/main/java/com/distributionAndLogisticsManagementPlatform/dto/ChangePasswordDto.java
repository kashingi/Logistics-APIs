package com.distributionAndLogisticsManagementPlatform.dto;

import lombok.Data;

//Add your annotations here
@Data
public class ChangePasswordDto {

    private String currentPassword;
    private String newPassword;
}

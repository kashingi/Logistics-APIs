package com.distributionAndLogisticsManagementPlatform.dto;

import lombok.Data;

//Add your annotations here
@Data
public class ResetPasswordDto {

    private String token;

    private String newPassword;
}

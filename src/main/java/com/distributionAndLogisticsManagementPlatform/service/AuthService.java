package com.distributionAndLogisticsManagementPlatform.service;

import com.distributionAndLogisticsManagementPlatform.dto.UserDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<String> signup(UserDto userDto);

    ResponseEntity<String> verifyEmail(String token);
}

package com.distributionAndLogisticsManagementPlatform.service;

import com.distributionAndLogisticsManagementPlatform.dto.UserDto;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<String> registerUser(UserDto userDto);
}

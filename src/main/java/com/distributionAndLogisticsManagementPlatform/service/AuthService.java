package com.distributionAndLogisticsManagementPlatform.service;

import com.distributionAndLogisticsManagementPlatform.dto.UserDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<String> signup(UserDto userDto);

    ResponseEntity<String> verifyEmail(String token);


    ResponseEntity<String> resendVerificationToken(String email);

    ResponseEntity<String> forgotPassword(String email);

    ResponseEntity<String> resetPassword(String token, String newPassword);

    ResponseEntity<String> changePassword(String email, String currentPassword, String newPassword);
}

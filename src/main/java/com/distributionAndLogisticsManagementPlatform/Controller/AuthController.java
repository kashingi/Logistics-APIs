package com.distributionAndLogisticsManagementPlatform.Controller;

import com.distributionAndLogisticsManagementPlatform.dto.ChangePasswordDto;
import com.distributionAndLogisticsManagementPlatform.dto.LoginDto;
import com.distributionAndLogisticsManagementPlatform.dto.ResetPasswordDto;
import com.distributionAndLogisticsManagementPlatform.dto.UserDto;
import com.distributionAndLogisticsManagementPlatform.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

//Add your annotations here
@RestController
@RequestMapping(path = "/v1/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(path = "/signup")
    public ResponseEntity<String> signup(@RequestBody UserDto userDto) {
        return authService.signup(userDto);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<String> login (@RequestBody LoginDto loginDto) {
        return authService.login(loginDto.getEmail(), loginDto.getPassword());
    }

    @PostMapping(path = "/verify-email")
    public  ResponseEntity<String> verifyEmail (@RequestParam String token) {
        return authService.verifyEmail(token);
    }

    @PostMapping(path = "/resend-verificationToken")
    public ResponseEntity<String> resendVerificationToken (@RequestBody UserDto userDto) {
        return authService.resendVerificationToken(userDto.getEmail());
    }

    @PostMapping(path = "/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody UserDto userDto) {
        return authService.forgotPassword(userDto.getEmail());
    }

    @PostMapping(path = "/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        return authService.resetPassword(resetPasswordDto.getToken(), resetPasswordDto.getNewPassword());
    }

    @PostMapping(path = "/change-password")
    public ResponseEntity<String> changePassword(Authentication authentication, @RequestBody ChangePasswordDto changePasswordDto) {
        String email = authentication.getName();

        return authService.changePassword(email, changePasswordDto.getCurrentPassword(), changePasswordDto.getNewPassword());
    }
}

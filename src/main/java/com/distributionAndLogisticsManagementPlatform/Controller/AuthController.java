package com.distributionAndLogisticsManagementPlatform.Controller;

import com.distributionAndLogisticsManagementPlatform.dto.UserDto;
import com.distributionAndLogisticsManagementPlatform.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PostMapping(path = "/verify-email")
    public  ResponseEntity<String> verifyEmail (@RequestParam String token) {
        return authService.verifyEmail(token);
    }

    @PostMapping(path = "/resend-verificationToken")
    public ResponseEntity<String> resendVerificationToken (@RequestBody UserDto userDto) {
        return authService.resendVerificationToken(userDto);
    }
}

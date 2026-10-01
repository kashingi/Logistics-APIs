package com.distributionAndLogisticsManagementPlatform.Controller;

import com.distributionAndLogisticsManagementPlatform.dto.UserDto;
import com.distributionAndLogisticsManagementPlatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//Add your annotations here
@RestController
@RequestMapping(path = "/v1/api/users")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping(path = "/register")
    public ResponseEntity<String> Register(@RequestBody UserDto userDto) {
        return userService.registerUser(userDto);
    }
}

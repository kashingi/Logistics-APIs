package com.distributionAndLogisticsManagementPlatform.serviceImpl;

import com.distributionAndLogisticsManagementPlatform.constants.LogisticConstants;
import com.distributionAndLogisticsManagementPlatform.dto.UserDto;
import com.distributionAndLogisticsManagementPlatform.entity.User;
import com.distributionAndLogisticsManagementPlatform.enums.Role;
import com.distributionAndLogisticsManagementPlatform.repository.UserRepository;
import com.distributionAndLogisticsManagementPlatform.service.UserService;
import com.distributionAndLogisticsManagementPlatform.utils.EmailUtils;
import com.distributionAndLogisticsManagementPlatform.utils.LogisticUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

//Add your annotations here
@Service
public class UserServiceImpl implements UserService {

    //Autowire here
    @Autowired
    private UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    EmailUtils emailUtils;
    @Override
    public ResponseEntity<String> registerUser(UserDto userDto) {
        try {
            if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
                return LogisticUtils.getResponseEntity("Email already exists", HttpStatus.BAD_REQUEST);
            }
            User user = new User();
            user.setFullName(userDto.getFullName());
            user.setEmail(userDto.getEmail());
            user.setRole(Role.USER);
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            user.setActive(false);
            user.setEmailVerified(false);
            user.setCreatedAt(Instant.now());
            user.setUpdatedAt(Instant.now());

            String verificationToken = UUID.randomUUID().toString();
            user.setVerificationToken(verificationToken);
            user.setVerificationTokenExpiry(Instant.now().plusSeconds(86400));

            //Save user
            userRepository.save(user);

            //Send Email to the user
            emailUtils.sendVerificationEmail(user.getEmail(), verificationToken);

            return LogisticUtils.getResponseEntity("User created successfully", HttpStatus.CREATED);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

package com.distributionAndLogisticsManagementPlatform.serviceImpl;

import com.distributionAndLogisticsManagementPlatform.constants.LogisticConstants;
import com.distributionAndLogisticsManagementPlatform.dto.UserDto;
import com.distributionAndLogisticsManagementPlatform.entity.User;
import com.distributionAndLogisticsManagementPlatform.enums.Role;
import com.distributionAndLogisticsManagementPlatform.repository.UserRepository;
import com.distributionAndLogisticsManagementPlatform.service.AuthService;
import com.distributionAndLogisticsManagementPlatform.utils.EmailUtils;
import com.distributionAndLogisticsManagementPlatform.utils.LogisticUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

//Add your annotations here
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    //Autowire here
    @Autowired
    private UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    EmailUtils emailUtils;
    @Override
    public ResponseEntity<String> signup(UserDto userDto) {
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

    public ResponseEntity<String> verifyEmail (String token) {
        try {
            User user = userRepository.findByVerificationToken(token);

            if (user == null) {
                return LogisticUtils.getResponseEntity("Invalid or expired verification token.", HttpStatus.BAD_REQUEST);
            }

            if (user.getVerificationTokenExpiry() == null || user.getVerificationTokenExpiry().isBefore(Instant.now())) {
                return LogisticUtils.getResponseEntity("Verification token has expired, please request a new one", HttpStatus.BAD_REQUEST);
            }
            user.setEmailVerified(true);
            user.setVerificationToken(null);
            user.setVerificationTokenExpiry(null);

            userRepository.save(user);

            return LogisticUtils.getResponseEntity("Email verified successfully! You can now login", HttpStatus.OK);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> resendVerificationToken(UserDto userDto) {
        try {
            Optional<User> optionalUser = userRepository.findByEmail(userDto.getEmail());

            if (optionalUser.isEmpty()) {
                return LogisticUtils.getResponseEntity("User not found", HttpStatus.NOT_FOUND);
            }

            User user = optionalUser.get();
            String verificationToken = UUID.randomUUID().toString();

            user.setVerificationToken(verificationToken);
            user.setVerificationTokenExpiry(Instant.now().plusSeconds(86400));

            userRepository.save(user);

            emailUtils.sendVerificationEmail(userDto.getEmail(), verificationToken);

            return LogisticUtils.getResponseEntity("Verification token resent successfully, please check your email", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

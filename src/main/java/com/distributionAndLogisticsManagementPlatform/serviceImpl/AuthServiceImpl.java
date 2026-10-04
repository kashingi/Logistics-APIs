package com.distributionAndLogisticsManagementPlatform.serviceImpl;

import com.distributionAndLogisticsManagementPlatform.constants.LogisticConstants;
import com.distributionAndLogisticsManagementPlatform.dto.UserDto;
import com.distributionAndLogisticsManagementPlatform.entity.User;
import com.distributionAndLogisticsManagementPlatform.enums.Role;
import com.distributionAndLogisticsManagementPlatform.repository.UserRepository;
import com.distributionAndLogisticsManagementPlatform.security.JwtUtil;
import com.distributionAndLogisticsManagementPlatform.service.AuthService;
import com.distributionAndLogisticsManagementPlatform.utils.EmailUtils;
import com.distributionAndLogisticsManagementPlatform.utils.LogisticUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
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
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    UserDetailsServiceImpl userDetailsService;

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

    @Override
    public ResponseEntity<String> login(String email, String password) {
        try {
            log.info("Inside login {}");
//            User user = userRepository.findByEmail(email)
//                    .filter(u -> passwordEncoder.matches(password, u.getPassword()))
//                    .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
            Optional <User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isEmpty() || !passwordEncoder.matches(password, optionalUser.get().getPassword())) {
                return LogisticUtils.getResponseEntity("Invalid email or password.", HttpStatus.UNAUTHORIZED);
            }
            User user = optionalUser.get();

            if (!user.isActive()) {
                return LogisticUtils.getResponseEntity("Your account has been deactivated. Please contact support for assistance", HttpStatus.BAD_REQUEST);
            }
            if (!user.isEmailVerified()) {
                return LogisticUtils.getResponseEntity("Please verify your email before logging in. Check your inbox for the verification link.", HttpStatus.UNAUTHORIZED);
            }
            final String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

            //String token = jwtUtil.generateToken(userDetailsService.loadUserByUsername(user.getEmail()), userDetailsService.getUserDetail().getRole());

            return new ResponseEntity<String>("{\"token\":\"" + token + "\"}", HttpStatus.OK);

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
    public ResponseEntity<String> resendVerificationToken(String email) {
        try {
            Optional<User> optionalUser = userRepository.findByEmail(email);

            if (optionalUser.isEmpty()) {
                return LogisticUtils.getResponseEntity("User not found", HttpStatus.NOT_FOUND);
            }

            User user = optionalUser.get();
            String verificationToken = UUID.randomUUID().toString();

            user.setVerificationToken(verificationToken);
            user.setVerificationTokenExpiry(Instant.now().plusSeconds(86400));

            userRepository.save(user);

            emailUtils.sendVerificationEmail(email, verificationToken);

            return LogisticUtils.getResponseEntity("Verification token resent successfully, please check your email", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(String email) {
        try {
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isEmpty()) {
                return LogisticUtils.getResponseEntity("User not found", HttpStatus.NOT_FOUND);
            }
            User user = optionalUser.get();
            String resetToken = UUID.randomUUID().toString();

            user.setPasswordResetToken(resetToken);
            user.setPasswordResetTokenExpiry(Instant.now().plusSeconds(3600));

            userRepository.save(user);
            emailUtils.sendPasswordResetEmail(email, resetToken);

            return LogisticUtils.getResponseEntity("Password reset email sent successfully. Please check your email inbox.", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> resetPassword(String token, String newPassword) {
        try {
            Optional<User> optionalUser = userRepository.findByPasswordResetToken(token);
            if (optionalUser.isEmpty()) {
                return LogisticUtils.getResponseEntity("Invalid or expired reset token", HttpStatus.BAD_REQUEST);
            }

            User user = optionalUser.get();
            if (user.getPasswordResetTokenExpiry() == null || user.getPasswordResetTokenExpiry().isBefore(Instant.now())) {
                return LogisticUtils.getResponseEntity("Reset token has expired", HttpStatus.BAD_REQUEST);
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            user.setPasswordResetToken(null);
            user.setPasswordResetTokenExpiry(null);

            userRepository.save(user);

            return LogisticUtils.getResponseEntity("Password reset successfully. You can now log in with your new password", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> changePassword(String email, String currentPassword, String newPassword) {
        try {
            Optional <User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isEmpty()) {
                return LogisticUtils.getResponseEntity("User not found", HttpStatus.NOT_FOUND);
            }
            User user = optionalUser.get();
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                return LogisticUtils.getResponseEntity("Current password is incorrect.", HttpStatus.BAD_REQUEST);
            }

            //Save the new credentials
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            return LogisticUtils.getResponseEntity("Password changed successfully.", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

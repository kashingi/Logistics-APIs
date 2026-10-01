package com.distributionAndLogisticsManagementPlatform.utils;

import com.distributionAndLogisticsManagementPlatform.constants.LogisticConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

//Add your annotations here
@Service
public class EmailUtils {

    private static final Logger logger = LoggerFactory.getLogger(EmailUtils.class);
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${app.frontendy.url:http://localhost:4200}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;
    @Autowired
    private MailSender mailSender;

    public ResponseEntity<String> sendVerificationEmail(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Logistics - Verify your Email");

            String verificationLink = frontendUrl + "/verify-email?token=" + token;

            String emailBody =
                    "Welcome to Netflix Movies!\n\n"
                            + "Thank you for registering. Please verify your email by clicking the link below : \n\n"
                            + verificationLink
                            + "\n\n"
                            + "This link will expire in 30 minutes.\n\n"
                            + "If you didn't create this account, please ignore this email.\n\n"
                            + "Best regards,\n"
                            + "Logistics Team";

            message.setText(emailBody);
            mailSender.send(message);

            logger.info("Verification email send to {} : {} ", toEmail);
        } catch (Exception ex) {
            logger.error("Failed to send verification email to {} : {} ", toEmail, ex.getMessage(), ex);

            return LogisticUtils.getResponseEntity("Failed to send verification email", HttpStatus.UNAUTHORIZED);
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<String> sendPasswordResetEmail(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Logistics - Reset Password");

            String resetLink = frontendUrl + "/reset-password?token=" + token;

            String emailBody =
                    "Greetings, \n\n"
                            + "We received a request to reset your password. Click the link below to reset it : \n\n"
                            + resetLink
                            + "\n\n"
                            + "This link will expire in 1 hour.\n\n"
                            + "If you did not request a password reset, please ignore this email.\n\n"
                            + "Best regards,\n"
                            + "Logistic Team";

            message.setText(emailBody);
            mailSender.send(message);

            logger.info("Password reset email sent to {} : {} ", toEmail);
        } catch (Exception ex) {
            logger.error("Failed to send password reset email to {} : {} ", toEmail, ex.getMessage(), ex);

            return LogisticUtils.getResponseEntity("Failed to send reset email", HttpStatus.UNAUTHORIZED);
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

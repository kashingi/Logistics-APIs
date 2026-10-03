package com.distributionAndLogisticsManagementPlatform.repository;

import com.distributionAndLogisticsManagementPlatform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//Add your annotations here
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    User findByVerificationToken(String verificationToken);
}

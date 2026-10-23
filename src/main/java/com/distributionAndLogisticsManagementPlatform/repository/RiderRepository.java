package com.distributionAndLogisticsManagementPlatform.repository;

import com.distributionAndLogisticsManagementPlatform.entity.Rider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//Add your annotations here
public interface RiderRepository extends JpaRepository<Rider, Long> {

    boolean existsByPhone(String phone);

    List<Rider> findByStatus(String status);

    List<Rider> findByCurrentZoneIdAndIsAvailableTrue(Long zoneId);

    List<Rider> findByIsAvailableTrue();
}

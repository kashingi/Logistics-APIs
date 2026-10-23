package com.distributionAndLogisticsManagementPlatform.repository;

import com.distributionAndLogisticsManagementPlatform.entity.DeliveryZone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//Add your annotations here
public interface DeliveryZoneRepository extends JpaRepository<DeliveryZone, Long> {

    boolean existsByName(String name);

    List<DeliveryZone> findByStatus(String status);
}

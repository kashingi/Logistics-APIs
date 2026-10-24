package com.distributionAndLogisticsManagementPlatform.repository;

import com.distributionAndLogisticsManagementPlatform.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//Add your annotations here
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Optional<Delivery> findBySalesOrderId(Long salesOrderId);
}

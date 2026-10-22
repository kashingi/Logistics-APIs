package com.distributionAndLogisticsManagementPlatform.repository;

import com.distributionAndLogisticsManagementPlatform.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

//Add your annotations here
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

}

package com.distributionAndLogisticsManagementPlatform.repository;

import com.distributionAndLogisticsManagementPlatform.entity.StockIn;
import org.springframework.data.jpa.repository.JpaRepository;

//Add your annotations here
public interface StockInRepository extends JpaRepository<StockIn, Long> {
}

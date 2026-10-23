package com.distributionAndLogisticsManagementPlatform.repository;

import com.distributionAndLogisticsManagementPlatform.entity.StockCheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//Add your annotations here
public interface StockCheckRepository extends JpaRepository<StockCheck, Long> {

    List<StockCheck> findByStatus(String status);
}

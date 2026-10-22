package com.distributionAndLogisticsManagementPlatform.repository;

import com.distributionAndLogisticsManagementPlatform.entity.StockOut;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//Add your annotations here
public interface StockOutRepository extends JpaRepository<StockOut, Long> {
    List<StockOut> findByApprovalStatus(String approvalStatus);
}

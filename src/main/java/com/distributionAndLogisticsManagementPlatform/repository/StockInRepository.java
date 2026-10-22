package com.distributionAndLogisticsManagementPlatform.repository;

import com.distributionAndLogisticsManagementPlatform.entity.GoodsReceivedNote;
import com.distributionAndLogisticsManagementPlatform.entity.StockIn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//Add your annotations here
public interface StockInRepository extends JpaRepository<StockIn, Long> {

    List<StockIn> findByApprovalStatus(String upperCase);
}

package com.distributionAndLogisticsManagementPlatform.repository;

import com.distributionAndLogisticsManagementPlatform.entity.StockTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//Add your annotations here
public interface StockTransferRepository extends JpaRepository<StockTransfer, Long> {

    List<StockTransfer> findByTransferStatus(String transferStatus);
}

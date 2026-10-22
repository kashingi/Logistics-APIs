package com.distributionAndLogisticsManagementPlatform.repository;

import com.distributionAndLogisticsManagementPlatform.entity.PurchaseReturn;
import org.springframework.data.jpa.repository.JpaRepository;

//Add your annotations here
public interface PurchaseReturnRepository extends JpaRepository<PurchaseReturn, Long> {
}

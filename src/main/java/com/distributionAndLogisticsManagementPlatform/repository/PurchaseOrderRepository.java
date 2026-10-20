package com.distributionAndLogisticsManagementPlatform.repository;

import com.distributionAndLogisticsManagementPlatform.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

//Add your annotations here
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

}

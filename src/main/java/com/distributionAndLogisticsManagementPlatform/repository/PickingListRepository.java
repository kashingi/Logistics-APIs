package com.distributionAndLogisticsManagementPlatform.repository;

import com.distributionAndLogisticsManagementPlatform.entity.PickingList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//Add your annotations here
public interface PickingListRepository extends JpaRepository<PickingList, Long> {

    List<PickingList> findByDeliveryType(String deliveryType);
}

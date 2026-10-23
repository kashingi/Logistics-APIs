package com.distributionAndLogisticsManagementPlatform.repository;

import com.distributionAndLogisticsManagementPlatform.entity.StockLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

//Add your annotations here
public interface StockLevelRepository extends JpaRepository<StockLevel, Long> {

    Optional<StockLevel> findByProductIdAndWarehouseIdAndBatchId(Long productId, Long warehouseId, Long batchId);

    Optional<StockLevel> findByProductIdAndWarehouseIdAndBatchIsNull(Long productId, Long warehouseId);

    List<StockLevel> findByProductIdAndWarehouseId(Long productId, Long warehouseId);

    List<StockLevel> findByWarehouseId(Long warehouseId);
}

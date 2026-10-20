package com.distributionAndLogisticsManagementPlatform.service;

import com.distributionAndLogisticsManagementPlatform.dto.WarehouseDto;
import org.springframework.http.ResponseEntity;

public interface WarehouseService {
    ResponseEntity<String> createWarehouse(WarehouseDto warehouseDto);

    ResponseEntity<?> getAllWarehouses();

    ResponseEntity<?> getWarehouseById(Long id);

    ResponseEntity<String> updateWarehouse(Long id, WarehouseDto warehouseDto);

    ResponseEntity<String> updateStatus(Long id, String status);
}

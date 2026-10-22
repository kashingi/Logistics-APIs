package com.distributionAndLogisticsManagementPlatform.service;

import com.distributionAndLogisticsManagementPlatform.dto.StockOutDto;
import org.springframework.http.ResponseEntity;

//Add your annotations here
public interface StockOutService {

    ResponseEntity<String> createStockOut(StockOutDto stockOutDto);

    ResponseEntity<?> getAllStockOuts();

    ResponseEntity<?> getStockOutById(Long id);

    ResponseEntity<?> getStockOutsByStatus(String approvalStatus);

    ResponseEntity<String> approveStockOut(Long id);

    ResponseEntity<String> rejectStockOut(Long id);
}

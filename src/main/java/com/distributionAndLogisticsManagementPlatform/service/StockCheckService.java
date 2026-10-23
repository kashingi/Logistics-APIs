package com.distributionAndLogisticsManagementPlatform.service;

import com.distributionAndLogisticsManagementPlatform.dto.StockCheckCountDto;
import com.distributionAndLogisticsManagementPlatform.dto.StockCheckDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

//Add your annotations here
public interface StockCheckService {
    ResponseEntity<String> createStockCheck(StockCheckDto stockCheckDto);

    ResponseEntity<?> getAllStockChecks();

    ResponseEntity<?> getStockCheckById(Long id);

    ResponseEntity<?> getStockChecksByStatus(String status);

    ResponseEntity<String> submitCounts(Long id, List<StockCheckCountDto> counts);

    ResponseEntity<String> completeStockCheck(Long id);

    ResponseEntity<String> approveStockCheck(Long id);
}

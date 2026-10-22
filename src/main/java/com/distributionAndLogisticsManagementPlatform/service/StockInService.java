package com.distributionAndLogisticsManagementPlatform.service;

import com.distributionAndLogisticsManagementPlatform.dto.StockInDto;
import org.springframework.http.ResponseEntity;

//Add your annotations here
public interface StockInService {


    ResponseEntity<String> createStockIn(StockInDto stockInDto);

    ResponseEntity<?> getAllStockIns();

    ResponseEntity<?> getStockInById(Long id);

    ResponseEntity<String> approveStockIn(Long id);

    ResponseEntity<String> rejectStockIn(Long id);
}

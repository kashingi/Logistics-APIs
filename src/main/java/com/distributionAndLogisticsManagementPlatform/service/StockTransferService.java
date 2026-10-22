package com.distributionAndLogisticsManagementPlatform.service;

import com.distributionAndLogisticsManagementPlatform.dto.StockTransferDto;
import org.springframework.http.ResponseEntity;

//Add your annotations here
public interface StockTransferService {
    ResponseEntity<String> createStockTransfer(StockTransferDto stockTransferDto);

    ResponseEntity<?> getAllStockTransfers();

    ResponseEntity<?> getStockTransferById(Long id);

    ResponseEntity<?> getStockTransfersByStatus(String transferStatus);

    ResponseEntity<String> approveStockTransfer(Long id);

    ResponseEntity<String> receiveStockTransfer(Long id);

    ResponseEntity<String> cancelStockTransfer(Long id);
}

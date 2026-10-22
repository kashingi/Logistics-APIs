package com.distributionAndLogisticsManagementPlatform.service;

import com.distributionAndLogisticsManagementPlatform.dto.PurchaseReturnDto;
import org.springframework.http.ResponseEntity;

//Add your annotations here
public interface PurchaseReturnService {
    ResponseEntity<String> createPurchaseReturn(PurchaseReturnDto purchaseReturnDto);

    ResponseEntity<?> getAllPurchaseReturns();

    ResponseEntity<?> getPurchaseReturnById(Long id);

    ResponseEntity<String> approvePurchaseReturn(Long id);

    ResponseEntity<String> rejectPurchaseReturn(Long id);
}

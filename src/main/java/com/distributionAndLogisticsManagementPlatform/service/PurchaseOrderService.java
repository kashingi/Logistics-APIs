package com.distributionAndLogisticsManagementPlatform.service;

import com.distributionAndLogisticsManagementPlatform.dto.PurchaseOrderDto;
import org.springframework.http.ResponseEntity;

//Add your annotations here
public interface PurchaseOrderService {
    ResponseEntity<String> createPurchaseOrder(PurchaseOrderDto purchaseOrderDto);

    ResponseEntity<?> getAllPurchaseOrders();

    ResponseEntity<?> getPurchaseOrderById(Long id);

    ResponseEntity<String> approvePurchaseOrder(Long id);

    ResponseEntity<String> rejectPurchaseOrder(Long id);
}



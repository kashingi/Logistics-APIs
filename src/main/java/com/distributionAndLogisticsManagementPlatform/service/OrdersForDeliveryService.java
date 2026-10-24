package com.distributionAndLogisticsManagementPlatform.service;

import org.springframework.http.ResponseEntity;

//Add your annotations here
public interface OrdersForDeliveryService {
    ResponseEntity<?> getOrdersForDelivery(String dispatchType);

    ResponseEntity<String> assignRider(Long salesOrderId, Long riderId);

    ResponseEntity<String> confirmLocation(Long salesOrderId, String notes);

    ResponseEntity<?> getPackingQueue(String dispatchType);

    ResponseEntity<String> allocateLot(Long salesOrderId);

    ResponseEntity<String> scanItem(Long salesOrderId, String barcode);
}

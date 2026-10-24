package com.distributionAndLogisticsManagementPlatform.service;

import org.springframework.http.ResponseEntity;

import java.util.List;

//Add your annotations here
public interface DeliveryService {
    ResponseEntity<?> getAllDeliveries(String deliveryType, String status);

    ResponseEntity<?> getDeliveryById(Long id);

    ResponseEntity<?> getDeliveriesInProgress();

    ResponseEntity<String> markAsPicked(Long deliveryId);

    ResponseEntity<String> markAsCollected(Long deliveryId);

    ResponseEntity<String> completeDelivery(Long deliveryId);

    ResponseEntity<String> cancelDelivery(Long deliveryId, String reason, String action);

    ResponseEntity<String> returnDelivery(Long deliveryId, String reason, String action);

    // ===================== PICKING LISTS =====================

    ResponseEntity<String> createPickingList(String deliveryType, Long riderId, Long zoneId, List<Long> salesOrderIds);

    ResponseEntity<?> getPickingLists(String deliveryType);

    ResponseEntity<String> markPickingListAsPicked(Long pickingListId);
}

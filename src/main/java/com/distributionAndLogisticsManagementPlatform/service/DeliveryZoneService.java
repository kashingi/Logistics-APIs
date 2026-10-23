package com.distributionAndLogisticsManagementPlatform.service;

import com.distributionAndLogisticsManagementPlatform.dto.DeliveryZoneDto;
import org.springframework.http.ResponseEntity;

//Add your annotations here
public interface DeliveryZoneService {
    ResponseEntity<String> createDeliveryZone(DeliveryZoneDto deliveryZoneDto);

    ResponseEntity<?> getAllDeliveryZones();

    ResponseEntity<?> getDeliveryZoneById(Long id);

    ResponseEntity<?> getDeliveryZonesByStatus(String status);

    ResponseEntity<String> updateDeliveryZone(Long id, DeliveryZoneDto deliveryZoneDto);

    ResponseEntity<String> updateStatus(Long id, String status);

    ResponseEntity<String> deleteDeliveryZone(Long id);
}

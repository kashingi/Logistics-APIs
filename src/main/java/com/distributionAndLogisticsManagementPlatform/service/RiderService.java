package com.distributionAndLogisticsManagementPlatform.service;

import com.distributionAndLogisticsManagementPlatform.dto.RiderDto;
import org.springframework.http.ResponseEntity;

//Add your annotations here
public interface RiderService {
    ResponseEntity<String> createRider(RiderDto riderDto);

    ResponseEntity<?> getAllRiders();

    ResponseEntity<?> getRiderById(Long id);

    ResponseEntity<?> getRidersByStatus(String status);

    ResponseEntity<?> getAvailableRiders(Long zoneId);

    ResponseEntity<String> updateRider(Long id, RiderDto riderDto);

    ResponseEntity<String> updateStatus(Long id, String status);
}

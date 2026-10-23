package com.distributionAndLogisticsManagementPlatform.serviceImpl;

import com.distributionAndLogisticsManagementPlatform.constants.LogisticConstants;
import com.distributionAndLogisticsManagementPlatform.dto.DeliveryZoneDto;
import com.distributionAndLogisticsManagementPlatform.entity.DeliveryZone;
import com.distributionAndLogisticsManagementPlatform.repository.DeliveryZoneRepository;
import com.distributionAndLogisticsManagementPlatform.security.JwtFilter;
import com.distributionAndLogisticsManagementPlatform.service.DeliveryZoneService;
import com.distributionAndLogisticsManagementPlatform.utils.LogisticUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

//Add your annotations
@Service
public class DeliveryZoneServiceImpl implements DeliveryZoneService {

    @Autowired
    private DeliveryZoneRepository deliveryZoneRepository;
    @Autowired
    private JwtFilter jwtFilter;


    @Override
    public ResponseEntity<String> createDeliveryZone(DeliveryZoneDto deliveryZoneDto) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

            if (deliveryZoneDto.getName() == null || deliveryZoneDto.getName().trim().isEmpty()) {
                return LogisticUtils.getResponseEntity("Zone name is required", HttpStatus.BAD_REQUEST);
            }
            if (deliveryZoneRepository.existsByName(deliveryZoneDto.getName().trim())) {
                return LogisticUtils.getResponseEntity("Delivery Zone name already exists", HttpStatus.BAD_REQUEST);
            }

            DeliveryZone zone = new DeliveryZone();
            zone.setName(deliveryZoneDto.getName().trim());
            zone.setLocations(deliveryZoneDto.getLocations() != null ? deliveryZoneDto.getLocations() : List.of());
            zone.setStatus("ACTIVE");
            zone.setCreatedBy(jwtFilter.getCurrentUser());
            zone.setUpdatedBy(jwtFilter.getCurrentUser());

            deliveryZoneRepository.save(zone);

            return LogisticUtils.getResponseEntity("Delivery Zone created successfully", HttpStatus.CREATED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getAllDeliveryZones() {
        try {
            List<DeliveryZone> deliveryZones = deliveryZoneRepository.findAll();

            return LogisticUtils.getResponseEntity("Delivery Zones retrieved successfully", deliveryZones, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getDeliveryZoneById(Long id) {
        try {
            Optional<DeliveryZone> deliveryZone = deliveryZoneRepository.findById(id);
            if (deliveryZone.isEmpty()) {
                return LogisticUtils.getResponseEntity("Delivery Zone not found", HttpStatus.NOT_FOUND);
            }
            return LogisticUtils.getResponseEntity("Delivery Zone retrieved successfully", deliveryZone.get(), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getDeliveryZonesByStatus(String status) {
        try {
            List<DeliveryZone> zones = deliveryZoneRepository.findByStatus(status.toUpperCase());

            return LogisticUtils.getResponseEntity("Delivery Zones retrieved successfully", zones, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateDeliveryZone(Long id, DeliveryZoneDto deliveryZoneDto) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
            Optional<DeliveryZone> deliveryZone = deliveryZoneRepository.findById(id);
            if (deliveryZone.isEmpty()) {
                return LogisticUtils.getResponseEntity("Delivery Zone not found", HttpStatus.NOT_FOUND);
            }

            DeliveryZone zone = deliveryZone.get();
            zone.setName(deliveryZoneDto.getName());
            zone.setLocations(deliveryZoneDto.getLocations());
            zone.setUpdatedBy(jwtFilter.getCurrentUser());
            zone.setUpdatedAt(Instant.now());

            deliveryZoneRepository.save(zone);

            return LogisticUtils.getResponseEntity("Delivery Zone updated successfully", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Long id, String status) {
        try {
            Optional<DeliveryZone> optional = deliveryZoneRepository.findById(id);
            if (optional.isEmpty()) {
                return LogisticUtils.getResponseEntity("Delivery Zone not found", HttpStatus.NOT_FOUND);
            }
            String newStatus = status.toUpperCase().trim();
            if (!newStatus.equals("ACTIVE") && !newStatus.equals("INACTIVE")) {
                return LogisticUtils.getResponseEntity("Invalid status. Use ACTIVE or INACTIVE", HttpStatus.BAD_REQUEST);
            }

            DeliveryZone zone = optional.get();
            zone.setStatus(newStatus);
            zone.setUpdatedBy(jwtFilter.getCurrentUser());
            zone.setUpdatedAt(Instant.now());
            deliveryZoneRepository.save(zone);

            return LogisticUtils.getResponseEntity("Delivery Zone status updated successfully", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteDeliveryZone(Long id) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
            Optional<DeliveryZone> optional = deliveryZoneRepository.findById(id);
            if (optional.isEmpty()) {
                return LogisticUtils.getResponseEntity("Delivery Zone not found", HttpStatus.NOT_FOUND);
            }

            // Soft delete
            DeliveryZone zone = optional.get();
            zone.setStatus("INACTIVE");
            zone.setUpdatedBy(jwtFilter.getCurrentUser());
            zone.setUpdatedAt(Instant.now());
            deliveryZoneRepository.save(zone);

            return LogisticUtils.getResponseEntity("Delivery Zone deleted (set to INACTIVE)", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

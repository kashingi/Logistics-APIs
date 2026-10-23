package com.distributionAndLogisticsManagementPlatform.serviceImpl;

import com.distributionAndLogisticsManagementPlatform.constants.LogisticConstants;
import com.distributionAndLogisticsManagementPlatform.dto.RiderDto;
import com.distributionAndLogisticsManagementPlatform.entity.DeliveryZone;
import com.distributionAndLogisticsManagementPlatform.entity.Rider;
import com.distributionAndLogisticsManagementPlatform.repository.DeliveryZoneRepository;
import com.distributionAndLogisticsManagementPlatform.repository.RiderRepository;
import com.distributionAndLogisticsManagementPlatform.security.JwtFilter;
import com.distributionAndLogisticsManagementPlatform.service.RiderService;
import com.distributionAndLogisticsManagementPlatform.utils.LogisticUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

//Add your annotations here
@Service
public class RiderServiceImpl implements RiderService {

    @Autowired
    private RiderRepository riderRepository;
    @Autowired
    private DeliveryZoneRepository deliveryZoneRepository;
    @Autowired
    private JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> createRider(RiderDto riderDto) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
            if (riderDto.getPhone() == null || riderDto.getPhone().trim().isEmpty()) {
                return LogisticUtils.getResponseEntity("Phone number is required", HttpStatus.BAD_REQUEST);
            }

            if (riderRepository.existsByPhone(riderDto.getPhone().trim())) {
                return LogisticUtils.getResponseEntity("Phone number already exists", HttpStatus.BAD_REQUEST);
            }

            Rider rider = new Rider();
            rider.setFirstName(riderDto.getFirstName());
            rider.setLastName(riderDto.getLastName());
            rider.setPhone(riderDto.getPhone().trim());
            rider.setNationalIdOrPassport(riderDto.getNationalIdOrPassport());
            rider.setDateJoined(riderDto.getDateJoined() != null ? riderDto.getDateJoined() : LocalDate.now());
            rider.setPassportPhotoUrl(riderDto.getPassportPhotoUrl());
            rider.setVehicleRegistrationNo(riderDto.getVehicleRegistrationNo());
            rider.setVehicleType(riderDto.getVehicleType());
            rider.setCarryingCapacityKg(riderDto.getCarryingCapacityKg());
            rider.setContractDurationMonths(riderDto.getContractDurationMonths());
            rider.setStatus("ACTIVE");
            rider.setAvailable(true);
            rider.setCreatedBy(jwtFilter.getCurrentUser());
            rider.setUpdatedBy(jwtFilter.getCurrentUser());

            if (riderDto.getCurrentZoneId() != null) {
                Optional<DeliveryZone> zoneOpt = deliveryZoneRepository.findById(riderDto.getCurrentZoneId());
                zoneOpt.ifPresent(rider::setCurrentZone);
            }

            riderRepository.save(rider);

            return LogisticUtils.getResponseEntity("Rider created successfully", HttpStatus.CREATED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getAllRiders() {
        try {
            List<Rider> riders = riderRepository.findAll();

            return LogisticUtils.getResponseEntity("Riders retrieved successfully", riders, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getRiderById(Long id) {
        try {
            Optional<Rider> rider = riderRepository.findById(id);
            if (rider.isEmpty()) {
                return LogisticUtils.getResponseEntity("Rider not found", HttpStatus.NOT_FOUND);
            }

            return LogisticUtils.getResponseEntity("Rider retrieved successfully", rider.get(), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getRidersByStatus(String status) {
        try {
            if (!status.trim().equalsIgnoreCase("ACTIVE") && !status.trim().equalsIgnoreCase("INACTIVE")) {
                return LogisticUtils.getResponseEntity("Invalid rider status", HttpStatus.BAD_REQUEST);
            }
            List<Rider> riders = riderRepository.findByStatus(status.toUpperCase());

            return LogisticUtils.getResponseEntity("Riders retrieved successfully", riders, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getAvailableRiders(Long zoneId) {
        try {
            List<Rider> riders;
            if (zoneId != null) {
                riders = riderRepository.findByCurrentZoneIdAndIsAvailableTrue(zoneId);
            } else {
                riders = riderRepository.findByIsAvailableTrue();
            }
            return LogisticUtils.getResponseEntity("Available riders retrieved successfully", riders, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateRider(Long id, RiderDto riderDto) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
            Optional<Rider> optional = riderRepository.findById(id);
            if (optional.isEmpty()) {
                return LogisticUtils.getResponseEntity("Rider not found", HttpStatus.NOT_FOUND);
            }
            Rider rider = optional.get();
            rider.setFirstName(riderDto.getFirstName());
            rider.setLastName(riderDto.getLastName());
            rider.setNationalIdOrPassport(riderDto.getNationalIdOrPassport());
            rider.setPassportPhotoUrl(riderDto.getPassportPhotoUrl());
            rider.setVehicleRegistrationNo(riderDto.getVehicleRegistrationNo());
            rider.setVehicleType(riderDto.getVehicleType());
            rider.setCarryingCapacityKg(riderDto.getCarryingCapacityKg());
            rider.setContractDurationMonths(riderDto.getContractDurationMonths());
            rider.setUpdatedBy(jwtFilter.getCurrentUser());
            rider.setUpdatedAt(Instant.now());

            if (riderDto.getCurrentZoneId() != null) {
                Optional<DeliveryZone> zoneOpt = deliveryZoneRepository.findById(riderDto.getCurrentZoneId());
                zoneOpt.ifPresent(rider::setCurrentZone);
            }
            riderRepository.save(rider);

            return LogisticUtils.getResponseEntity("Rider updated successfully", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Long id, String status) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
            Optional<Rider> optional = riderRepository.findById(id);
            if (optional.isEmpty()) {
                return LogisticUtils.getResponseEntity("Rider not found", HttpStatus.NOT_FOUND);
            }
            String newStatus = status.toUpperCase().trim();
            if (!newStatus.equals("ACTIVE") && !newStatus.equals("INACTIVE")) {
                return LogisticUtils.getResponseEntity("Invalid status. Use ACTIVE or INACTIVE", HttpStatus.BAD_REQUEST);
            }

            Rider rider = optional.get();
            rider.setStatus(newStatus);

            // If suspended, make unavailable
            if (newStatus.equals("INACTIVE")) {
                rider.setAvailable(false);
            }

            rider.setUpdatedBy(jwtFilter.getCurrentUser());
            rider.setUpdatedAt(Instant.now());
            riderRepository.save(rider);

            return LogisticUtils.getResponseEntity("Rider status updated successfully", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

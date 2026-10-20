package com.distributionAndLogisticsManagementPlatform.serviceImpl;

import com.distributionAndLogisticsManagementPlatform.constants.LogisticConstants;
import com.distributionAndLogisticsManagementPlatform.dto.WarehouseDto;
import com.distributionAndLogisticsManagementPlatform.entity.Warehouse;
import com.distributionAndLogisticsManagementPlatform.repository.WarehouseRepository;
import com.distributionAndLogisticsManagementPlatform.security.JwtFilter;
import com.distributionAndLogisticsManagementPlatform.service.WarehouseService;
import com.distributionAndLogisticsManagementPlatform.utils.LogisticUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class WarehouseServiceImpl implements WarehouseService {

    @Autowired
    private JwtFilter jwtFilter;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Override
    public ResponseEntity<String> createWarehouse(WarehouseDto warehouseDto) {
        try {
            if (jwtFilter.isAdmin()) {
                if (warehouseDto.getName() == null || warehouseDto.getName().trim().isEmpty()) {
                    return LogisticUtils.getResponseEntity("Warehouse name is required", HttpStatus.BAD_REQUEST);
                }

                if (warehouseRepository.existsByName(warehouseDto.getName().trim())) {
                    return LogisticUtils.getResponseEntity("Warehouse name already exists", HttpStatus.BAD_REQUEST);
                }
                if (warehouseDto.getStreet() == null || warehouseDto.getStreet().trim().isEmpty()) {
                    return LogisticUtils.getResponseEntity("Street name is required.", HttpStatus.BAD_REQUEST);
                }
                if (warehouseRepository.existsByStreet(warehouseDto.getStreet().trim())) {
                    return LogisticUtils.getResponseEntity("Warehouse in this street already exists.", HttpStatus.BAD_REQUEST);
                }

                Warehouse warehouse = new Warehouse();
                warehouse.setWarehouseNo("WH" + String.format("%04d", warehouseRepository.count() + 1));
                warehouse.setName(warehouseDto.getName().trim());
                warehouse.setContactPerson(warehouseDto.getContactPerson());
                warehouse.setPhone(warehouseDto.getPhone());
                warehouse.setEmail(warehouseDto.getEmail());
                warehouse.setStreet(warehouseDto.getStreet());
                warehouse.setCity(warehouseDto.getCity());
                warehouse.setPostal(warehouseDto.getPostal());
                warehouse.setCategory(warehouseDto.getCategory());
                warehouse.setAllowNegativeInventory(warehouseDto.isAllowNegativeInventory());
                warehouse.setRemark(warehouseDto.getRemark());
                warehouse.setStatus("ACTIVE");
                warehouse.setCreatedAt(Instant.now());
                warehouse.setUpdatedAt(Instant.now());

                warehouseRepository.save(warehouse);

                return LogisticUtils.getResponseEntity("Warehouse created successfully", HttpStatus.CREATED);
            }
            return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getAllWarehouses() {
        try {
            List<Warehouse> warehouses = warehouseRepository.findAll();

            return LogisticUtils.getResponseEntity("Warehouses retrieved successfully.", warehouses, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getWarehouseById(Long id) {
        try {
            Optional<Warehouse> warehouse = warehouseRepository.findById(id);
            if (warehouse.isEmpty()) {
                return LogisticUtils.getResponseEntity("Warehouse not found.", HttpStatus.NOT_FOUND);
            }

            return LogisticUtils.getResponseEntity("Warehouse retrieved successfully.", warehouse, HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateWarehouse(Long id, WarehouseDto warehouseDto) {
        try {
            if (jwtFilter.isAdmin()) {
                Optional<Warehouse> optional = warehouseRepository.findById(id);
                if (optional.isEmpty()) {
                    return LogisticUtils.getResponseEntity("Warehouse not found", HttpStatus.NOT_FOUND);
                }

                Warehouse warehouse = optional.get();
                warehouse.setName(warehouseDto.getName());
                warehouse.setContactPerson(warehouseDto.getContactPerson());
                warehouse.setPhone(warehouseDto.getPhone());
                warehouse.setEmail(warehouseDto.getEmail());
                warehouse.setStreet(warehouseDto.getStreet());
                warehouse.setCity(warehouseDto.getCity());
                warehouse.setPostal(warehouseDto.getPostal());
                warehouse.setCategory(warehouseDto.getCategory());
                warehouse.setAllowNegativeInventory(warehouseDto.isAllowNegativeInventory());
                warehouse.setRemark(warehouseDto.getRemark());
                warehouse.setStatus(warehouseDto.getStatus());
                warehouse.setUpdatedAt(Instant.now());

                warehouseRepository.save(warehouse);

                return LogisticUtils.getResponseEntity("Warehouse updated successfully", HttpStatus.OK);
            }
            return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Long id, String status) {
        try {
            if (jwtFilter.isAdmin()) {

                Optional<Warehouse> optional = warehouseRepository.findById(id);
                if (optional.isEmpty()) {
                    return LogisticUtils.getResponseEntity("Warehouse not found", HttpStatus.NOT_FOUND);
                }

                Warehouse warehouse = optional.get();
                warehouse.setStatus(status);
                warehouse.setUpdatedAt(Instant.now());
                warehouseRepository.save(warehouse);

                return LogisticUtils.getResponseEntity("Warehouse status updated successfully", HttpStatus.OK);
            }
            return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

package com.distributionAndLogisticsManagementPlatform.serviceImpl;

import com.distributionAndLogisticsManagementPlatform.constants.LogisticConstants;
import com.distributionAndLogisticsManagementPlatform.dto.SupplierDto;
import com.distributionAndLogisticsManagementPlatform.entity.StockIn;
import com.distributionAndLogisticsManagementPlatform.entity.Supplier;
import com.distributionAndLogisticsManagementPlatform.repository.SupplierRepository;
import com.distributionAndLogisticsManagementPlatform.security.JwtFilter;
import com.distributionAndLogisticsManagementPlatform.service.SupplierService;
import com.distributionAndLogisticsManagementPlatform.utils.LogisticUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

//Add your annotations here
@Service
@Slf4j
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> createSupplier(SupplierDto supplierDto) {
        try {
            if (jwtFilter.isAdmin()) {
                if (supplierDto.getName() == null || supplierDto.getName().trim().isEmpty()) {
                    return LogisticUtils.getResponseEntity("Supplier name is required", HttpStatus.BAD_REQUEST);
                }
                if (supplierRepository.existsByName(supplierDto.getName().trim())) {
                    return LogisticUtils.getResponseEntity("Supplier name already exists", HttpStatus.BAD_REQUEST);
                }

                Supplier supplier = new Supplier();
                supplier.setSupplierNo("SUP" + String.format("%04d", supplierRepository.count() + 1));
                supplier.setName(supplierDto.getName().trim());
                supplier.setContactPerson(supplierDto.getContactPerson());
                supplier.setPhone(supplierDto.getPhone());
                supplier.setEmail(supplierDto.getEmail());
                supplier.setPaymentTerms(supplierDto.getPaymentTerms());
                supplier.setStreet(supplierDto.getStreet());
                supplier.setCity(supplierDto.getCity());
                supplier.setPostal(supplierDto.getPostal());
                supplier.setStatus("ACTIVE");
                supplier.setCreatedAt(Instant.now());
                supplier.setUpdatedAt(Instant.now());

                supplierRepository.save(supplier);

                return LogisticUtils.getResponseEntity("Supplier created successfully", HttpStatus.CREATED);
            }
            return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getAllSuppliers() {
        try {
            List<Supplier> suppliers = supplierRepository.findAll();

            return LogisticUtils.getResponseEntity("Suppliers retrieved successfully.", suppliers, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getSupplierById(Long id) {
        try {
            Optional<Supplier> supplier = supplierRepository.findById(id);
            if (supplier.isEmpty()) {
                return LogisticUtils.getResponseEntity("Supplier not found", HttpStatus.NOT_FOUND);
            }
            return LogisticUtils.getResponseEntity("Supplier retrieved successfully.", supplier, HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getSupplierByStatus(String status) {
        try {
            if (!status.trim().equalsIgnoreCase("ACTIVE") && !status.trim().equalsIgnoreCase("INACTIVE")) {
                return LogisticUtils.getResponseEntity("Invalid supplier status", HttpStatus.BAD_REQUEST);
            }
            List<Supplier> suppliers = supplierRepository.findByStatus(status);

            return LogisticUtils.getResponseEntity("Suppliers retrieved successfully.", suppliers, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateSupplier(Long id, SupplierDto supplierDto) {
        try {
            if (jwtFilter.isAdmin()) {
                Optional<Supplier> optional = supplierRepository.findById(id);
                if (optional.isEmpty()) {
                    return LogisticUtils.getResponseEntity("Supplier not found", HttpStatus.NOT_FOUND);
                }

                Supplier supplier = optional.get();
                supplier.setName(supplierDto.getName());
                supplier.setContactPerson(supplierDto.getContactPerson());
                supplier.setPhone(supplierDto.getPhone());
                supplier.setEmail(supplierDto.getEmail());
                supplier.setPaymentTerms(supplierDto.getPaymentTerms());
                supplier.setStreet(supplierDto.getStreet());
                supplier.setCity(supplierDto.getCity());
                supplier.setPostal(supplierDto.getPostal());
                supplier.setStatus(supplierDto.getStatus());
                supplier.setUpdatedAt(Instant.now());

                supplierRepository.save(supplier);

                return LogisticUtils.getResponseEntity("Supplier updated successfully", HttpStatus.OK);
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
                Optional<Supplier> optional = supplierRepository.findById(id);
                if (optional.isEmpty()) {
                    return LogisticUtils.getResponseEntity("Supplier not found", HttpStatus.NOT_FOUND);
                }

                Supplier supplier = optional.get();
                supplier.setStatus(status);
                supplier.setUpdatedAt(Instant.now());
                supplierRepository.save(supplier);

                return LogisticUtils.getResponseEntity("Supplier status updated successfully", HttpStatus.OK);
            }
            return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

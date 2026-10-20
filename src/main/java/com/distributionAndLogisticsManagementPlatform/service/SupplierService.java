package com.distributionAndLogisticsManagementPlatform.service;

import com.distributionAndLogisticsManagementPlatform.dto.SupplierDto;
import org.springframework.http.ResponseEntity;

//Add your annotations here
public interface SupplierService {


    ResponseEntity<String> createSupplier(SupplierDto supplierDto);

    ResponseEntity<?> getAllSuppliers();

    ResponseEntity<?> getSupplierById(Long id);

    ResponseEntity<String> updateSupplier(Long id, SupplierDto supplierDto);

    ResponseEntity<String> updateStatus(Long id, String status);
}

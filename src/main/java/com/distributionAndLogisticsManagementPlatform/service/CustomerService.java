package com.distributionAndLogisticsManagementPlatform.service;

import com.distributionAndLogisticsManagementPlatform.dto.CustomerDto;
import org.springframework.http.ResponseEntity;

//Add your annotations here
public interface CustomerService {
    ResponseEntity<?> addCustomer(CustomerDto customerDto);

    ResponseEntity<?> getAllCustomers();

    ResponseEntity<?> getCustomerById(Long id);

    ResponseEntity<?> getCustomersByStatus(String status);

    ResponseEntity<?> searchCustomers(String keyword);

    ResponseEntity<?> updateCustomer(Long id, CustomerDto customerDto);

    ResponseEntity<?> updateCustomerStatus(Long id, String status);
}

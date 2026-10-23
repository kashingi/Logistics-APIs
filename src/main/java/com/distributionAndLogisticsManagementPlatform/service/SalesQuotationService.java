package com.distributionAndLogisticsManagementPlatform.service;

import com.distributionAndLogisticsManagementPlatform.dto.SalesQuotationDto;
import org.springframework.http.ResponseEntity;

//Add your annotations here
public interface SalesQuotationService {
    ResponseEntity<String> createSalesQuotation(SalesQuotationDto salesQuotationDto);

    ResponseEntity<?> getAllSalesQuotations();

    ResponseEntity<?> getSalesQuotationById(Long id);

    ResponseEntity<?> getSalesQuotationsByStatus(String status);

    ResponseEntity<String> cancelSalesQuotation(Long id);

    ResponseEntity<String> convertToSalesOrder(Long id);
}

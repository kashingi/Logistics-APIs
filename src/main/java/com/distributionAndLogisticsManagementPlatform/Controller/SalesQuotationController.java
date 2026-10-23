package com.distributionAndLogisticsManagementPlatform.Controller;

import com.distributionAndLogisticsManagementPlatform.dto.SalesQuotationDto;
import com.distributionAndLogisticsManagementPlatform.service.SalesQuotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//Add your annotations here
@CrossOrigin
@RestController
@RequestMapping(path = "v1/api/sales-quotations")
public class SalesQuotationController {

    @Autowired
    private SalesQuotationService salesQuotationService;

    @PostMapping("/addSalesQuotation")
    public ResponseEntity<String> create(@RequestBody SalesQuotationDto salesQuotationDto) {
        return salesQuotationService.createSalesQuotation(salesQuotationDto);
    }

    @GetMapping("/getAllSalesQuotations")
    public ResponseEntity<?> getAll() {
        return salesQuotationService.getAllSalesQuotations();
    }

    @GetMapping("/getSalesQuotationById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return salesQuotationService.getSalesQuotationById(id);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getByStatus(@PathVariable String status) {
        return salesQuotationService.getSalesQuotationsByStatus(status);
    }

    @PatchMapping("/cancelSalesQuotation/{id}")
    public ResponseEntity<String> cancelSalesQuotation(@PathVariable Long id) {
        return salesQuotationService.cancelSalesQuotation(id);
    }

    @PostMapping("/convert-to-sales-order/{id}")
    public ResponseEntity<String> convertToSalesOrder(@PathVariable Long id) {
        return salesQuotationService.convertToSalesOrder(id);
    }
}

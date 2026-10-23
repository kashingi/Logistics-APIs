package com.distributionAndLogisticsManagementPlatform.service;

import com.distributionAndLogisticsManagementPlatform.dto.SalesOrderDto;
import org.springframework.http.ResponseEntity;

//Add your annotations here
public interface SalesOrderService {
    ResponseEntity<String> createSalesOrder(SalesOrderDto salesOrderDto);

    ResponseEntity<?> getAllSalesOrders();

    ResponseEntity<?> getSalesOrderById(Long id);

    ResponseEntity<?> getSalesOrdersByStatus(String status);

    ResponseEntity<String> confirmSalesOrder(Long id);

    ResponseEntity<String> cancelSalesOrder(Long id);
}

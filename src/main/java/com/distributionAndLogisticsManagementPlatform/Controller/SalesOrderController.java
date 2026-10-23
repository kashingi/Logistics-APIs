package com.distributionAndLogisticsManagementPlatform.Controller;

import com.distributionAndLogisticsManagementPlatform.dto.SalesOrderDto;
import com.distributionAndLogisticsManagementPlatform.service.SalesOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//Add your annotations here
@CrossOrigin
@RestController
@RequestMapping(path = "/v1/api/sales-orders")
public class SalesOrderController {

    @Autowired
    private SalesOrderService salesOrderService;

    @PostMapping("/addSalesOrder")
    public ResponseEntity<String> create(@RequestBody SalesOrderDto salesOrderDto) {
        return salesOrderService.createSalesOrder(salesOrderDto);
    }

    @GetMapping("/getAllSalesOrders")
    public ResponseEntity<?> getAll() {
        return salesOrderService.getAllSalesOrders();
    }

    @GetMapping("/getSalesOrderById/{id}")
    public ResponseEntity<?> getSalesOrderById(@PathVariable Long id) {
        return salesOrderService.getSalesOrderById(id);
    }

    @GetMapping("/getSalesOrdersByStatus/{status}")
    public ResponseEntity<?> getByStatus(@PathVariable String status) {
        return salesOrderService.getSalesOrdersByStatus(status);
    }

    @PatchMapping("/confirmSalesOrder/{id}")
    public ResponseEntity<String> confirmSalesOrder(@PathVariable Long id) {
        return salesOrderService.confirmSalesOrder(id);
    }

    @PatchMapping("/cancel/{id}")
    public ResponseEntity<String> cancel(@PathVariable Long id) {
        return salesOrderService.cancelSalesOrder(id);
    }

    @GetMapping("/getPendingSalesOrders")
    public ResponseEntity<?> getPendingSalesOrders() {
        return salesOrderService.getPendingSalesOrders();
    }

    @PatchMapping("/{id}/dispatch")
    public ResponseEntity<String> moveToDispatch(@PathVariable Long id, @RequestParam String dispatchType) {  // STANDARD or EXPRESS
        return salesOrderService.moveToDispatch(id, dispatchType);
    }

    @PatchMapping("/{id}/collection")
    public ResponseEntity<String> moveToCollection(@PathVariable Long id) {
        return salesOrderService.moveToCollection(id);
    }

    @PatchMapping("/{id}/cancel-with-credit-note")
    public ResponseEntity<String> cancelWithCreditNote(@PathVariable Long id, @RequestParam(required = false) String reason) {
        return salesOrderService.cancelWithCreditNote(id, reason);
    }
}

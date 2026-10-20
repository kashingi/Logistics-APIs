package com.distributionAndLogisticsManagementPlatform.Controller;

import com.distributionAndLogisticsManagementPlatform.dto.PurchaseOrderDto;
import com.distributionAndLogisticsManagementPlatform.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//Add your annotations here
@RestController
@CrossOrigin
@RequestMapping(path = "/v1/api/purchase-orders")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @PostMapping("/addPurchaseOrder")
    public ResponseEntity<String> createPurchaseOrder(@RequestBody PurchaseOrderDto purchaseOrderDto) {
        return purchaseOrderService.createPurchaseOrder(purchaseOrderDto);
    }

    @GetMapping("/getAllPurchaseOrders")
    public ResponseEntity<?> getAllPurchaseOrders() {
        return purchaseOrderService.getAllPurchaseOrders();
    }

    @GetMapping("/getPurchaseOrderById/{id}")
    public ResponseEntity<?> getPurchaseOrderById(@PathVariable Long id) {
        return purchaseOrderService.getPurchaseOrderById(id);
    }

    @PatchMapping("/approve/{id}")
    public ResponseEntity<String> approvePurchaseOrder(@PathVariable Long id) {
        return purchaseOrderService.approvePurchaseOrder(id);
    }

    @PatchMapping("/reject/{id}")
    public ResponseEntity<String> rejectPurchaseOrder(@PathVariable Long id) {
        return purchaseOrderService.rejectPurchaseOrder(id);
    }
}

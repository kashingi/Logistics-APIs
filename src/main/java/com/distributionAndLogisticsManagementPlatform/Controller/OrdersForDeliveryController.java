package com.distributionAndLogisticsManagementPlatform.Controller;

import com.distributionAndLogisticsManagementPlatform.service.OrdersForDeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//Add your annotations here
@CrossOrigin
@RestController
@RequestMapping(path = "/v1/api/orders-for-delivery")
public class OrdersForDeliveryController {

    @Autowired
    private OrdersForDeliveryService ordersForDeliveryService;

    @GetMapping
    public ResponseEntity<?> getOrdersForDelivery(@RequestParam(required = false) String dispatchType) {
        return ordersForDeliveryService.getOrdersForDelivery(dispatchType);
    }

    @PostMapping("/{salesOrderId}/assign-rider")
    public ResponseEntity<String> assignRider(@PathVariable Long salesOrderId, @RequestParam Long riderId) {
        return ordersForDeliveryService.assignRider(salesOrderId, riderId);
    }

    @PostMapping("/{salesOrderId}/confirm-location")
    public ResponseEntity<String> confirmLocation(@PathVariable Long salesOrderId, @RequestParam(required = false) String notes) {
        return ordersForDeliveryService.confirmLocation(salesOrderId, notes);
    }

    @GetMapping("/packing-queue")
    public ResponseEntity<?> getPackingQueue(@RequestParam(required = false) String dispatchType) {
        return ordersForDeliveryService.getPackingQueue(dispatchType);
    }

    @PostMapping("/{salesOrderId}/allocate-lot")
    public ResponseEntity<String> allocateLot(@PathVariable Long salesOrderId) {
        return ordersForDeliveryService.allocateLot(salesOrderId);
    }

    @PostMapping("/{salesOrderId}/scan")
    public ResponseEntity<String> scanItem(@PathVariable Long salesOrderId, @RequestParam String barcode) {
        return ordersForDeliveryService.scanItem(salesOrderId, barcode);
    }
}

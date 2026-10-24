package com.distributionAndLogisticsManagementPlatform.Controller;

import com.distributionAndLogisticsManagementPlatform.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

//Add your annotations here
@CrossOrigin
@RestController
@RequestMapping(path = "/v1/api/deliveries")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    // ========== Deliveries ==========

    @GetMapping
    public ResponseEntity<?> getAllDeliveries(@RequestParam(required = false) String deliveryType, @RequestParam(required = false) String status) {
        return deliveryService.getAllDeliveries(deliveryType, status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return deliveryService.getDeliveryById(id);
    }

    @GetMapping("/in-progress")
    public ResponseEntity<?> getInProgress() {
        return deliveryService.getDeliveriesInProgress();
    }

    @PatchMapping("/{id}/mark-picked")
    public ResponseEntity<String> markAsPicked(@PathVariable Long deliveryId) {
        return deliveryService.markAsPicked(deliveryId);
    }

    @PatchMapping("/{id}/mark-collected")
    public ResponseEntity<String> markAsCollected(@PathVariable Long deliveryId) {
        return deliveryService.markAsCollected(deliveryId);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<String> complete(@PathVariable Long deliveryId) {
        return deliveryService.completeDelivery(deliveryId);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<String> cancel(@PathVariable Long deliveryId, @RequestParam String reason, @RequestParam String action) {   // CHARGE_CUSTOMER or RETURN_TO_PENDING
        return deliveryService.cancelDelivery(deliveryId, reason, action);
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<String> returnDelivery(@PathVariable Long deliveryId, @RequestParam String reason, @RequestParam String action) {   // REPLACE_DELIVERY or ISSUE_CREDIT_NOTE
        return deliveryService.returnDelivery(deliveryId, reason, action);
    }

    // ========== Picking Lists ==========

    @PostMapping("/picking-lists")
    public ResponseEntity<String> createPickingList(@RequestBody Map<String, Object> body) {
        String deliveryType = (String) body.get("deliveryType");
        Long riderId = body.get("riderId") != null ? Long.valueOf(body.get("riderId").toString()) : null;
        Long zoneId = body.get("zoneId") != null ? Long.valueOf(body.get("zoneId").toString()) : null;
        @SuppressWarnings("unchecked")
        List<Long> salesOrderIds = (List<Long>) body.get("salesOrderIds");

        return deliveryService.createPickingList(deliveryType, riderId, zoneId, salesOrderIds);
    }

    @GetMapping("/picking-lists")
    public ResponseEntity<?> getPickingLists(@RequestParam(required = false) String deliveryType) {
        return deliveryService.getPickingLists(deliveryType);
    }

    @PatchMapping("/picking-lists/{id}/mark-picked")
    public ResponseEntity<String> markPickingListAsPicked(@PathVariable Long pickingListId) {
        return deliveryService.markPickingListAsPicked(pickingListId);
    }
}

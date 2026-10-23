package com.distributionAndLogisticsManagementPlatform.Controller;

import com.distributionAndLogisticsManagementPlatform.dto.DeliveryZoneDto;
import com.distributionAndLogisticsManagementPlatform.service.DeliveryZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//Add your annotations here
@CrossOrigin
@RestController
@RequestMapping(path = "/v1/api/delivery-zones")
public class DeliveryZoneController {

    @Autowired
    private DeliveryZoneService deliveryZoneService;

    @PostMapping("/addDeliveryZone")
    public ResponseEntity<String> create(@RequestBody DeliveryZoneDto deliveryZoneDto) {
        return deliveryZoneService.createDeliveryZone(deliveryZoneDto);
    }

    @GetMapping("/getAllDeliveryZones")
    public ResponseEntity<?> getAll() {
        return deliveryZoneService.getAllDeliveryZones();
    }

    @GetMapping("/getDeliveryZoneById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return deliveryZoneService.getDeliveryZoneById(id);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getByStatus(@PathVariable String status) {
        return deliveryZoneService.getDeliveryZonesByStatus(status);
    }

    @PutMapping("/updateDeliveryZone/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody DeliveryZoneDto deliveryZoneDto) {
        return deliveryZoneService.updateDeliveryZone(id, deliveryZoneDto);
    }

    @PatchMapping("/updateStatus/{id}")
    public ResponseEntity<String> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return deliveryZoneService.updateStatus(id, status);
    }

    @DeleteMapping("/deleteDeliveryZone/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return deliveryZoneService.deleteDeliveryZone(id);
    }
}

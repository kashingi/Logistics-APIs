package com.distributionAndLogisticsManagementPlatform.Controller;

import com.distributionAndLogisticsManagementPlatform.dto.WarehouseDto;
import com.distributionAndLogisticsManagementPlatform.entity.Warehouse;
import com.distributionAndLogisticsManagementPlatform.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//Add your annotations here
@RestController
@CrossOrigin
@RequestMapping(path = "/v1/api/warehouse")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    @PostMapping("/addWarehouse")
    public ResponseEntity<String> createWarehouse(@RequestBody WarehouseDto warehouseDto) {
        return warehouseService.createWarehouse(warehouseDto);
    }

    @GetMapping("/getAllWarehouses")
    public ResponseEntity<?> getAllWarehouses() {
        return warehouseService.getAllWarehouses();
    }

    @GetMapping("/getWarehouseById/{id}")
    public ResponseEntity<?> getWarehouseById(@PathVariable Long id) {
        return warehouseService.getWarehouseById(id);
    }

    @PatchMapping("/updateWarehouse/{id}")
    public ResponseEntity<String> updateWarehouse(@PathVariable Long id, @RequestBody WarehouseDto warehouseDto) {
        return warehouseService.updateWarehouse(id, warehouseDto);
    }

    @PatchMapping("/updateWarehouseStatus/{id}")
    public ResponseEntity<String> updateStatus(@PathVariable Long id, @RequestBody WarehouseDto warehouseDto) {
        return warehouseService.updateStatus(id, warehouseDto.getStatus());
    }
}

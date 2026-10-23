package com.distributionAndLogisticsManagementPlatform.Controller;

import com.distributionAndLogisticsManagementPlatform.dto.SupplierDto;
import com.distributionAndLogisticsManagementPlatform.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//Add your annotations here
@RestController
@CrossOrigin
@RequestMapping(path = "/v1/api/supplers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @PostMapping("/addSupplier")
    public ResponseEntity<String> createSupplier(@RequestBody SupplierDto supplierDto) {
        return supplierService.createSupplier(supplierDto);
    }

    @GetMapping("/getAllSuppliers")
    public ResponseEntity<?> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }

    @GetMapping("/getSupplierById/{id}")
    public ResponseEntity<?> getSupplierById(@PathVariable Long id) {

        return supplierService.getSupplierById(id);
    }

    @GetMapping("/getSupplierByStatus/{status}")
    public ResponseEntity<?> getSupplierByStatus(@PathVariable String status) {

        return supplierService.getSupplierByStatus(status);
    }

    @PatchMapping("/updateSupplier/{id}")
    public ResponseEntity<String> updateSupplier(@PathVariable Long id, @RequestBody SupplierDto supplierDto) {
        return supplierService.updateSupplier(id, supplierDto);
    }

    @PatchMapping("/updateSupplierStatus/{id}")
    public ResponseEntity<String> updateStatus(@PathVariable Long id, @RequestBody SupplierDto supplierDto) {
        return supplierService.updateStatus(id, supplierDto.getStatus());
    }
}

package com.distributionAndLogisticsManagementPlatform.Controller;

import com.distributionAndLogisticsManagementPlatform.dto.PurchaseReturnDto;
import com.distributionAndLogisticsManagementPlatform.service.PurchaseReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//Add your annotations here
@CrossOrigin
@RestController
@RequestMapping(path = "/v1/api/purchase-returns")
public class PurchaseReturnController {

    @Autowired
    private PurchaseReturnService purchaseReturnService;

    @PostMapping("/addPurchaseReturn")
    public ResponseEntity<String> create(@RequestBody PurchaseReturnDto purchaseReturnDto) {
        return purchaseReturnService.createPurchaseReturn(purchaseReturnDto);
    }

    @GetMapping("/getAllPurchaseReturns")
    public ResponseEntity<?> getAll() {
        return purchaseReturnService.getAllPurchaseReturns();
    }

    @GetMapping("/getPurchaseReturnById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return purchaseReturnService.getPurchaseReturnById(id);
    }

    @PatchMapping("/approve/{id}")
    public ResponseEntity<String> approve(@PathVariable Long id) {
        return purchaseReturnService.approvePurchaseReturn(id);
    }

    @PatchMapping("/reject/{id}")
    public ResponseEntity<String> reject(@PathVariable Long id) {
        return purchaseReturnService.rejectPurchaseReturn(id);
    }
}

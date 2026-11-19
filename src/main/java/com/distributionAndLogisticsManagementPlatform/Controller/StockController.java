package com.distributionAndLogisticsManagementPlatform.Controller;

import com.distributionAndLogisticsManagementPlatform.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

//Add your annotations here
@CrossOrigin
@RestController
@RequestMapping(path = "/v1/api/stocks")
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping("/getAllStockLevels")
    public ResponseEntity<?> getAllStockLevels() {
        return stockService.getAllStockLevels();
    }

    @GetMapping("/getStockByWarehouse/{warehouseId}")
    public ResponseEntity<?> getByWarehouse(@PathVariable Long warehouseId) {
        return stockService.getStockByWarehouse(warehouseId);
    }

    @GetMapping("/getStockByProductId/{productId}")
    public ResponseEntity<?> getByProduct(@PathVariable Long productId) {
        return stockService.getStockByProduct(productId);
    }

    @GetMapping("/low-stock")
    public ResponseEntity<?> getLowStock(
            @RequestParam(defaultValue = "10") BigDecimal threshold) {
        return stockService.getLowStock(threshold);
    }

    // optional: keep ageing here or in ReportsController
    @GetMapping("/ageing")
    public ResponseEntity<?> getStockAgeing(
            @RequestParam(required = false) Long warehouseId) {
        return stockService.getStockAgeingReport(warehouseId);
    }
}

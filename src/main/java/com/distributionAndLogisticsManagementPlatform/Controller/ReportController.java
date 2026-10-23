package com.distributionAndLogisticsManagementPlatform.Controller;

import com.distributionAndLogisticsManagementPlatform.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//Add your annotations here
@CrossOrigin
@RestController
@RequestMapping(path = "/v1/api/reports")
public class ReportController {

    @Autowired
    private StockService stockService;

    @GetMapping("/stock-ageing")
    public ResponseEntity<?> getStockAgeingReport(@RequestParam(required = false) Long warehouseId) {
        return stockService.getStockAgeingReport(warehouseId);
    }
}

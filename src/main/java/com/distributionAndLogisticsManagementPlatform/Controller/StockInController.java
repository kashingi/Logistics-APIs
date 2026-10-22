package com.distributionAndLogisticsManagementPlatform.Controller;

import com.distributionAndLogisticsManagementPlatform.dto.StockInDto;
import com.distributionAndLogisticsManagementPlatform.service.StockInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//Add your annotations here
@CrossOrigin
@RestController
@RequestMapping(path = "/v1/api/stock-ins")
public class StockInController {

    @Autowired
    private StockInService stockInService;

    @PostMapping("/addStockIn")
    public ResponseEntity<String> create(@RequestBody StockInDto stockInDto) {
        return stockInService.createStockIn(stockInDto);
    }

    @GetMapping("/getAllStockIns")
    public ResponseEntity<?> getAll() {
        return stockInService.getAllStockIns();
    }

    @GetMapping("/getStockInById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return stockInService.getStockInById(id);
    }

    @PatchMapping("/approve/{id}")
    public ResponseEntity<String> approve(@PathVariable Long id) {
        return stockInService.approveStockIn(id);
    }

    @PatchMapping("/reject/{id}")
    public ResponseEntity<String> reject(@PathVariable Long id) {
        return stockInService.rejectStockIn(id);
    }
}

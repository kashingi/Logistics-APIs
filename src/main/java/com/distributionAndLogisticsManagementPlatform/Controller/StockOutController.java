package com.distributionAndLogisticsManagementPlatform.Controller;

import com.distributionAndLogisticsManagementPlatform.dto.StockOutDto;
import com.distributionAndLogisticsManagementPlatform.service.StockOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//Add your annotations here
@CrossOrigin
@RestController
@RequestMapping(path = "/v1/api/stock-outs")
public class StockOutController {

    @Autowired
    private StockOutService stockOutService;

    @PostMapping("/addStockOut")
    public ResponseEntity<String> create(@RequestBody StockOutDto stockOutDto) {
        return stockOutService.createStockOut(stockOutDto);
    }

    @GetMapping("/getAllStockOuts")
    public ResponseEntity<?> getAll() {
        return stockOutService.getAllStockOuts();
    }

    @GetMapping("/getStockOutById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return stockOutService.getStockOutById(id);
    }

    @GetMapping("/getByStatus/{approvalStatus}")
    public ResponseEntity<?> getByStatus(@PathVariable String approvalStatus) {
        return stockOutService.getStockOutsByStatus(approvalStatus);
    }

    @PatchMapping("/approve/{id}")
    public ResponseEntity<String> approve(@PathVariable Long id) {
        return stockOutService.approveStockOut(id);
    }

    @PatchMapping("/reject/{id}")
    public ResponseEntity<String> reject(@PathVariable Long id) {
        return stockOutService.rejectStockOut(id);
    }
}

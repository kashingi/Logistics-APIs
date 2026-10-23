package com.distributionAndLogisticsManagementPlatform.Controller;

import com.distributionAndLogisticsManagementPlatform.dto.StockCheckCountDto;
import com.distributionAndLogisticsManagementPlatform.dto.StockCheckDto;
import com.distributionAndLogisticsManagementPlatform.service.StockCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Add your annotations here
@CrossOrigin
@RestController
@RequestMapping(path = "/v1/api/stock-checks")
public class StockCheckController {

    @Autowired
    private StockCheckService stockCheckService;

    @PostMapping("/addStockCheck")
    public ResponseEntity<String> create(@RequestBody StockCheckDto stockCheckDto) {
        return stockCheckService.createStockCheck(stockCheckDto);
    }

    @GetMapping("/getAllStockChecks")
    public ResponseEntity<?> getAll() {
        return stockCheckService.getAllStockChecks();
    }

    @GetMapping("/getStockCheckById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return stockCheckService.getStockCheckById(id);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getByStatus(@PathVariable String status) {
        return stockCheckService.getStockChecksByStatus(status);
    }

    @PostMapping("/{id}/submit-counts")
    public ResponseEntity<String> submitCounts(@PathVariable Long id,
                                               @RequestBody List<StockCheckCountDto> counts) {
        return stockCheckService.submitCounts(id, counts);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<String> complete(@PathVariable Long id) {
        return stockCheckService.completeStockCheck(id);
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<String> approve(@PathVariable Long id) {
        return stockCheckService.approveStockCheck(id);
    }
}

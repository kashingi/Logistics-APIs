package com.distributionAndLogisticsManagementPlatform.Controller;

import com.distributionAndLogisticsManagementPlatform.dto.StockTransferDto;
import com.distributionAndLogisticsManagementPlatform.service.StockTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//Add your annotations here
@CrossOrigin
@RestController
@RequestMapping(path = "/v1/api/stock-transfers")
public class StockTransferController {

    @Autowired
    private StockTransferService stockTransferService;

    @PostMapping("/addStockTransfer")
    public ResponseEntity<String> create(@RequestBody StockTransferDto stockTransferDto) {
        return stockTransferService.createStockTransfer(stockTransferDto);
    }

    @GetMapping("/getAllStockTransfers")
    public ResponseEntity<?> getAll() {
        return stockTransferService.getAllStockTransfers();
    }

    @GetMapping("/getStockTransferById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return stockTransferService.getStockTransferById(id);
    }

    @GetMapping("/getStockTransfersByStatus/{transferStatus}")
    public ResponseEntity<?> getByStatus(@PathVariable String transferStatus) {
        return stockTransferService.getStockTransfersByStatus(transferStatus);
    }

    @PatchMapping("/approve/{id}")
    public ResponseEntity<String> approve(@PathVariable Long id) {
        return stockTransferService.approveStockTransfer(id);
    }

    @PatchMapping("/receive/{id}")
    public ResponseEntity<String> receive(@PathVariable Long id) {
        return stockTransferService.receiveStockTransfer(id);
    }

    @PatchMapping("/cancel/{id}")
    public ResponseEntity<String> cancel(@PathVariable Long id) {
        return stockTransferService.cancelStockTransfer(id);
    }
}

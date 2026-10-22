package com.distributionAndLogisticsManagementPlatform.Controller;

import com.distributionAndLogisticsManagementPlatform.dto.GoodsReceivedNoteDto;
import com.distributionAndLogisticsManagementPlatform.service.GoodsReceivedNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//Add your annotations here
@CrossOrigin
@RestController
@RequestMapping(path = "/v1/api/goods-received-notes")
public class GoodsReceivedNoteController {

    @Autowired
    private GoodsReceivedNoteService grnService;

    @PostMapping("/addGrn")
    public ResponseEntity<String> createGrn(@RequestBody GoodsReceivedNoteDto goodsReceivedNoteDto) {
        return grnService.createGrn(goodsReceivedNoteDto);
    }

    @GetMapping("/getAllGrns")
    public ResponseEntity<?> getAllGrns() {
        return grnService.getAllGrns();
    }

    @GetMapping("/getGrnById/{id}")
    public ResponseEntity<?> getGrnById(@PathVariable Long id) {
        return grnService.getGrnById(id);
    }

    @GetMapping("/getByStatus/{status}")
    public ResponseEntity<?> getByStatus(@PathVariable String status) {
        return grnService.getGrnsByStatus(status);
    }

    @PatchMapping("/approve/{id}")
    public ResponseEntity<String> approveGrn(@PathVariable Long id) {
        return grnService.approveGrn(id);
    }
}

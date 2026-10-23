package com.distributionAndLogisticsManagementPlatform.Controller;

import com.distributionAndLogisticsManagementPlatform.dto.RiderDto;
import com.distributionAndLogisticsManagementPlatform.service.RiderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//add your annotations here
@CrossOrigin
@RestController
@RequestMapping(path = "/v1/api/riders")
public class RiderController {

    @Autowired
    private RiderService riderService;

    @PostMapping("/addRider")
    public ResponseEntity<String> create(@RequestBody RiderDto riderDto) {
        return riderService.createRider(riderDto);
    }

    @GetMapping("/getAllRiders")
    public ResponseEntity<?> getAll() {
        return riderService.getAllRiders();
    }

    @GetMapping("/getRiderById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return riderService.getRiderById(id);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getByStatus(@PathVariable String status) {
        return riderService.getRidersByStatus(status);
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAvailableRiders(@RequestParam(required = false) Long zoneId) {
        return riderService.getAvailableRiders(zoneId);
    }

    @PutMapping("/updateRider/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody RiderDto riderDto) {
        return riderService.updateRider(id, riderDto);
    }

    @PatchMapping("/updateStatus/{id}")
    public ResponseEntity<String> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return riderService.updateStatus(id, status);
    }
}

package com.distributionAndLogisticsManagementPlatform.Controller;

import com.distributionAndLogisticsManagementPlatform.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//Add your annotations here
@CrossOrigin
@RestController
@RequestMapping(path = "/v1/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<?> getSummary(@RequestParam(required = false, defaultValue = "30") Integer days) {
        return dashboardService.getSummary(days);
    }
}

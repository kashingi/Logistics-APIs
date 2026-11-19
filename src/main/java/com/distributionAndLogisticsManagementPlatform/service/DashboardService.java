package com.distributionAndLogisticsManagementPlatform.service;

import org.springframework.http.ResponseEntity;

//Add your annotations here
public interface DashboardService {
    ResponseEntity<?> getSummary(Integer days);
}

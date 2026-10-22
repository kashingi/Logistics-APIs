package com.distributionAndLogisticsManagementPlatform.service;

import com.distributionAndLogisticsManagementPlatform.entity.Product;
import com.distributionAndLogisticsManagementPlatform.entity.Warehouse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

//Add your annotations here
public interface StockService {

    void increaseStock(Product product, Warehouse warehouse, BigDecimal quantity,
                       String batchNo, String movementType, String referenceType,
                       Long referenceId, String createdBy);

    void decreaseStock(Product product, Warehouse warehouse, BigDecimal quantity,
                       String batchNo, String movementType, String referenceType,
                       Long referenceId, String createdBy);
}

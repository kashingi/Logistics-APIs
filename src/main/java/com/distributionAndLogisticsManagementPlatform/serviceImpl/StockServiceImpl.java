package com.distributionAndLogisticsManagementPlatform.serviceImpl;

import com.distributionAndLogisticsManagementPlatform.constants.LogisticConstants;
import com.distributionAndLogisticsManagementPlatform.dto.StockAgeingReportResponse;
import com.distributionAndLogisticsManagementPlatform.dto.StockAgeingResponse;
import com.distributionAndLogisticsManagementPlatform.entity.*;
import com.distributionAndLogisticsManagementPlatform.repository.BatchRepository;
import com.distributionAndLogisticsManagementPlatform.repository.StockLevelRepository;
import com.distributionAndLogisticsManagementPlatform.repository.StockMovementRepository;
import com.distributionAndLogisticsManagementPlatform.service.StockService;
import com.distributionAndLogisticsManagementPlatform.utils.LogisticUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

//Add your annotations here
@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private StockLevelRepository stockLevelRepository;
    @Autowired
    private StockMovementRepository stockMovementRepository;
    @Autowired
    private BatchRepository batchRepository;

    /**
     * Increase stock (used by GRN, Stock In, Transfer Receive)
     */
    @Override
    public void increaseStock(Product product, Warehouse warehouse, BigDecimal quantity, String batchNo, String movementType, String referenceType, Long referenceId, String createdBy) {

        Batch batch = null;

        if (batchNo != null && !batchNo.isBlank()) {
            batch = batchRepository.findByLotNumber(batchNo).orElseGet(() -> {
                Batch newBatch = new Batch();
                newBatch.setLotNumber(batchNo);
                newBatch.setProduct(product);
                newBatch.setWarehouse(warehouse);
                newBatch.setQuantityReceived(quantity);
                newBatch.setQuantityRemaining(quantity);
                newBatch.setReceivedDate(LocalDate.now());

                // Calculate expiry if product has shelf life
                if (product.getShelfLifeDays() != null && product.getShelfLifeDays() > 0) {
                    newBatch.setExpiryDate(LocalDate.now().plusDays(product.getShelfLifeDays()));
                }
                return batchRepository.save(newBatch);
            });

            // Update remaining quantity if batch already existed
            batch.setQuantityRemaining(batch.getQuantityRemaining().add(quantity));
            batchRepository.save(batch);
        }

        // Update or create StockLevel
        StockLevel stockLevel = findOrCreateStockLevel(product, warehouse, batch);
        stockLevel.setQuantityOnHand(stockLevel.getQuantityOnHand().add(quantity));
        stockLevelRepository.save(stockLevel);

        // Write to ledger
        createMovement(product, warehouse, batch, quantity, movementType, referenceType, referenceId, createdBy);
    }

    /**
     * Decrease stock (used by Stock Out, Transfer Approve, Sales, etc.)
     */
    @Override
    public void decreaseStock(Product product, Warehouse warehouse, BigDecimal quantity, String batchNo, String movementType, String referenceType, Long referenceId, String createdBy) {

        Batch batch = null;
        if (batchNo != null && !batchNo.isBlank()) {
            batch = batchRepository.findByLotNumber(batchNo)
                    .orElseThrow(() -> new RuntimeException("Batch not found: " + batchNo));
        }

        StockLevel stockLevel = findOrCreateStockLevel(product, warehouse, batch);

        if (stockLevel.getQuantityOnHand().compareTo(quantity) < 0) {
            // Check if warehouse allows negative stock
            if (!warehouse.isAllowNegativeInventory()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
        }

        stockLevel.setQuantityOnHand(stockLevel.getQuantityOnHand().subtract(quantity));
        stockLevelRepository.save(stockLevel);

        if (batch != null) {
            batch.setQuantityRemaining(batch.getQuantityRemaining().subtract(quantity));
            batchRepository.save(batch);
        }

        // Write negative quantity to ledger
        createMovement(product, warehouse, batch, quantity.negate(), movementType, referenceType, referenceId, createdBy);
    }

    private StockLevel findOrCreateStockLevel(Product product, Warehouse warehouse, Batch batch) {
        Optional<StockLevel> optional;

        if (batch != null) {
            optional = stockLevelRepository.findByProductIdAndWarehouseIdAndBatchId(
                    product.getId(), warehouse.getId(), batch.getId());
        } else {
            optional = stockLevelRepository.findByProductIdAndWarehouseIdAndBatchIsNull(
                    product.getId(), warehouse.getId());
        }

        return optional.orElseGet(() -> {
            StockLevel newLevel = new StockLevel();
            newLevel.setProduct(product);
            newLevel.setWarehouse(warehouse);
            newLevel.setBatch(batch);
            newLevel.setQuantityOnHand(BigDecimal.ZERO);
            return stockLevelRepository.save(newLevel);
        });
    }

    private void createMovement(Product product, Warehouse warehouse, Batch batch,
                                BigDecimal quantity, String movementType,
                                String referenceType, Long referenceId, String createdBy) {

        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setWarehouse(warehouse);
        movement.setBatch(batch);
        movement.setQuantity(quantity);
        movement.setMovementType(movementType);
        movement.setReferenceType(referenceType);
        movement.setReferenceId(referenceId);
        movement.setCreatedBy(createdBy);

        stockMovementRepository.save(movement);
    }

    @Override
    public ResponseEntity<?> getStockAgeingReport(Long warehouseId) {
        try {
            List<StockLevel> stockLevels;

            if (warehouseId != null) {
                stockLevels = stockLevelRepository.findByWarehouseId(warehouseId);
            } else {
                stockLevels = stockLevelRepository.findAll();
            }

            // Configurable buckets (you can move this to application.properties later)
            List<String> buckets = List.of("0-15", "16-30", "31-60", "61+");

            Map<String, StockAgeingResponse> reportMap = new HashMap<>();
            BigDecimal grandTotal = BigDecimal.ZERO;

            String warehouseName = "All Warehouses";

            for (StockLevel level : stockLevels) {
                if (level.getQuantityOnHand().compareTo(BigDecimal.ZERO) <= 0) continue;

                Product product = level.getProduct();
                Batch batch = level.getBatch();

                String key = product.getId() + "-" + (level.getWarehouse() != null ? level.getWarehouse().getId() : "0");

                StockAgeingResponse row = reportMap.getOrDefault(key, new StockAgeingResponse());
                row.setProductName(product.getName());
                row.setSku(product.getSkuCode());
                row.setBarcode(product.getSerialNumber());
                row.setBrand(product.getBrand());
                row.setWarehouseName(level.getWarehouse().getName());

                if (warehouseId != null) {
                    warehouseName = level.getWarehouse().getName();
                }

                BigDecimal qty = level.getQuantityOnHand();
                row.setStockOnHand(
                        row.getStockOnHand() == null ? qty : row.getStockOnHand().add(qty)
                );

                // Calculate ageing bucket
                String bucket = "61+";
                if (batch != null && batch.getReceivedDate() != null) {
                    long days = java.time.temporal.ChronoUnit.DAYS.between(batch.getReceivedDate(), LocalDate.now());

                    if (days <= 15) bucket = "0-15";
                    else if (days <= 30) bucket = "16-30";
                    else if (days <= 60) bucket = "31-60";
                    else bucket = "61+";
                }

                if (row.getByBucket() == null) {
                    row.setByBucket(new HashMap<>());
                    for (String b : buckets) {
                        row.getByBucket().put(b, BigDecimal.ZERO);
                    }
                }

                row.getByBucket().put(bucket,
                        row.getByBucket().get(bucket).add(qty));

                reportMap.put(key, row);
                grandTotal = grandTotal.add(qty);
            }

            StockAgeingReportResponse report = new StockAgeingReportResponse();
            report.setWarehouse(warehouseName);
            report.setAgeingBuckets(buckets);
            report.setRows(new ArrayList<>(reportMap.values()));
            report.setTotalStockOnHand(grandTotal);

            return LogisticUtils.getResponseEntity("Stock Ageing report generated successfully", report, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

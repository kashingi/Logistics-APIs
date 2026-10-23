package com.distributionAndLogisticsManagementPlatform.serviceImpl;

import com.distributionAndLogisticsManagementPlatform.constants.LogisticConstants;
import com.distributionAndLogisticsManagementPlatform.dto.StockCheckCountDto;
import com.distributionAndLogisticsManagementPlatform.dto.StockCheckDto;
import com.distributionAndLogisticsManagementPlatform.entity.*;
import com.distributionAndLogisticsManagementPlatform.repository.ProductRepository;
import com.distributionAndLogisticsManagementPlatform.repository.StockCheckRepository;
import com.distributionAndLogisticsManagementPlatform.repository.StockLevelRepository;
import com.distributionAndLogisticsManagementPlatform.repository.WarehouseRepository;
import com.distributionAndLogisticsManagementPlatform.security.JwtFilter;
import com.distributionAndLogisticsManagementPlatform.service.StockCheckService;
import com.distributionAndLogisticsManagementPlatform.service.StockService;
import com.distributionAndLogisticsManagementPlatform.utils.LogisticUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

//Add your annotations here
@Service
public class StockCheckServiceImpl implements StockCheckService {

    @Autowired
    private StockCheckRepository stockCheckRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private StockLevelRepository stockLevelRepository;
    @Autowired
    private StockService stockService;
    @Autowired
    private JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> createStockCheck(StockCheckDto stockCheckDto) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

            Optional<Warehouse> warehouseOpt = warehouseRepository.findById(stockCheckDto.getWarehouseId());
            if (warehouseOpt.isEmpty()) {
                return LogisticUtils.getResponseEntity("Warehouse not found", HttpStatus.BAD_REQUEST);
            }

            if (stockCheckDto.getProductIds() == null || stockCheckDto.getProductIds().isEmpty()) {
                return LogisticUtils.getResponseEntity("At least one product must be selected", HttpStatus.BAD_REQUEST);
            }

            StockCheck stockCheck = new StockCheck();
            stockCheck.setStockCheckNo("SC" + String.format("%05d", stockCheckRepository.count() + 1));
            stockCheck.setDate(stockCheckDto.getDate() != null ? stockCheckDto.getDate() : LocalDate.now());
            stockCheck.setTaskType(stockCheckDto.getTaskType());
            stockCheck.setWarehouse(warehouseOpt.get());
            stockCheck.setDescription(stockCheckDto.getDescription());
            stockCheck.setStatus("PENDING");
            stockCheck.setCreatedBy(jwtFilter.getCurrentUser());
            stockCheck.setUpdatedBy(jwtFilter.getCurrentUser());

            // Create lines with current system quantity
            for (Long productId : stockCheckDto.getProductIds()) {
                Optional<Product> productOpt = productRepository.findById(productId);
                if (productOpt.isEmpty()) continue;

                Product product = productOpt.get();

                // Get current stock levels for this product in the warehouse
                List<StockLevel> levels = stockLevelRepository
                        .findByProductIdAndWarehouseId(productId, stockCheckDto.getWarehouseId());

                if (levels.isEmpty()) {
                    // No stock exists yet – still create a line with 0
                    StockCheckLine line = new StockCheckLine();
                    line.setProduct(product);
                    line.setSystemQuantity(BigDecimal.ZERO);
                    line.setCountedQuantity(null);
                    line.setVariance(null);
                    stockCheck.addLine(line);
                } else {
                    for (StockLevel level : levels) {
                        StockCheckLine line = new StockCheckLine();
                        line.setProduct(product);
                        line.setBatch(level.getBatch());
                        line.setSystemQuantity(level.getQuantityOnHand());
                        line.setCountedQuantity(null);
                        line.setVariance(null);
                        stockCheck.addLine(line);
                    }
                }
            }

            stockCheckRepository.save(stockCheck);

            return LogisticUtils.getResponseEntity(
                    "Stock Check created successfully: " + stockCheck.getStockCheckNo(),
                    HttpStatus.CREATED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getAllStockChecks() {
        try {

            List<StockCheck> stockChecks = stockCheckRepository.findAll();

            return LogisticUtils.getResponseEntity("Stock Checks retrieved successfully", stockChecks, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getStockCheckById(Long id) {
        try {
            Optional<StockCheck> stockCheck = stockCheckRepository.findById(id);
            if (stockCheck.isEmpty()) {
                return LogisticUtils.getResponseEntity("Stock Check not found", HttpStatus.NOT_FOUND);
            }
            return LogisticUtils.getResponseEntity("Stock Check retrieved successfully", stockCheck.get(), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getStockChecksByStatus(String status) {
        try {
            if (!status.trim().equalsIgnoreCase("PENDING") && !status.trim().equalsIgnoreCase("APPROVED") && !status.trim().equalsIgnoreCase("COUNTING_IN_PROGRESS") && !status.trim().equalsIgnoreCase("PENDING_DISCREPANCIES") && !status.trim().equalsIgnoreCase("COMPLETED")) {
                return LogisticUtils.getResponseEntity("Invalid goods received note status", HttpStatus.BAD_REQUEST);
            }
            List<StockCheck> stockChecks = stockCheckRepository.findByStatus(status);
            return LogisticUtils.getResponseEntity("Stock Checks retrieved successfully", stockChecks, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> submitCounts(Long id, List<StockCheckCountDto> counts) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

            Optional<StockCheck> optional = stockCheckRepository.findById(id);
            if (optional.isEmpty()) {
                return LogisticUtils.getResponseEntity("Stock Check not found", HttpStatus.NOT_FOUND);
            }

            StockCheck stockCheck = optional.get();

            if (!"PENDING".equals(stockCheck.getStatus()) &&
                    !"COUNTING_IN_PROGRESS".equals(stockCheck.getStatus())) {
                return LogisticUtils.getResponseEntity("Cannot submit counts for this status", HttpStatus.BAD_REQUEST);
            }

            for (StockCheckCountDto countDto : counts) {
                for (StockCheckLine line : stockCheck.getLines()) {
                    boolean match = line.getProduct().getId().equals(countDto.getProductId());
                    if (countDto.getBatchId() != null && line.getBatch() != null) {
                        match = match && line.getBatch().getId().equals(countDto.getBatchId());
                    }

                    if (match) {
                        line.setCountedQuantity(countDto.getCountedQuantity());
                        line.setVariance(countDto.getCountedQuantity().subtract(line.getSystemQuantity()));
                    }
                }
            }

            stockCheck.setStatus("COUNTING_IN_PROGRESS");
            stockCheck.setUpdatedBy(jwtFilter.getCurrentUser());
            stockCheck.setUpdatedAt(Instant.now());
            stockCheckRepository.save(stockCheck);

            return LogisticUtils.getResponseEntity("Counts submitted successfully", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> completeStockCheck(Long id) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

            Optional<StockCheck> optional = stockCheckRepository.findById(id);
            if (optional.isEmpty()) {
                return LogisticUtils.getResponseEntity("Stock Check not found", HttpStatus.NOT_FOUND);
            }

            StockCheck stockCheck = optional.get();

            boolean hasVariance = stockCheck.getLines().stream()
                    .anyMatch(line -> line.getVariance() != null &&
                            line.getVariance().compareTo(BigDecimal.ZERO) != 0);

            if (hasVariance) {
                stockCheck.setStatus("PENDING_DISCREPANCIES");
            } else {
                stockCheck.setStatus("COMPLETED");
            }

            stockCheck.setUpdatedBy(jwtFilter.getCurrentUser());
            stockCheck.setUpdatedAt(Instant.now());
            stockCheckRepository.save(stockCheck);

            return LogisticUtils.getResponseEntity("Stock Check completed. Status: " + stockCheck.getStatus(), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> approveStockCheck(Long id) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

            Optional<StockCheck> optional = stockCheckRepository.findById(id);
            if (optional.isEmpty()) {
                return LogisticUtils.getResponseEntity("Stock Check not found", HttpStatus.NOT_FOUND);
            }

            StockCheck stockCheck = optional.get();

            if (!"PENDING_DISCREPANCIES".equals(stockCheck.getStatus()) && !"COMPLETED".equals(stockCheck.getStatus())) {
                return LogisticUtils.getResponseEntity("Stock Check is not ready for approval", HttpStatus.BAD_REQUEST);
            }

            // Adjust stock according to variance
            for (StockCheckLine line : stockCheck.getLines()) {
                if (line.getVariance() != null && line.getVariance().compareTo(BigDecimal.ZERO) != 0) {

                    if (line.getVariance().compareTo(BigDecimal.ZERO) > 0) {
                        // Surplus → increase stock
                        stockService.increaseStock(
                                line.getProduct(),
                                stockCheck.getWarehouse(),
                                line.getVariance(),
                                line.getBatch() != null ? line.getBatch().getLotNumber() : null,
                                "STOCK_CHECK_ADJUSTMENT",
                                "STOCK_CHECK",
                                stockCheck.getId(),
                                jwtFilter.getCurrentUser()
                        );
                    } else {
                        // Shortage → decrease stock
                        stockService.decreaseStock(
                                line.getProduct(),
                                stockCheck.getWarehouse(),
                                line.getVariance().abs(),
                                line.getBatch() != null ? line.getBatch().getLotNumber() : null,
                                "STOCK_CHECK_ADJUSTMENT",
                                "STOCK_CHECK",
                                stockCheck.getId(),
                                jwtFilter.getCurrentUser()
                        );
                    }
                }
            }

            stockCheck.setStatus("APPROVED");
            stockCheck.setUpdatedBy(jwtFilter.getCurrentUser());
            stockCheck.setUpdatedAt(Instant.now());
            stockCheckRepository.save(stockCheck);

            return LogisticUtils.getResponseEntity("Stock Check approved. Stock levels have been adjusted.", HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

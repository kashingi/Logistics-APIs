package com.distributionAndLogisticsManagementPlatform.serviceImpl;

import com.distributionAndLogisticsManagementPlatform.constants.LogisticConstants;
import com.distributionAndLogisticsManagementPlatform.dto.StockCheckCountDto;
import com.distributionAndLogisticsManagementPlatform.dto.StockCheckDto;
import com.distributionAndLogisticsManagementPlatform.entity.*;
import com.distributionAndLogisticsManagementPlatform.repository.*;
import com.distributionAndLogisticsManagementPlatform.security.JwtFilter;
import com.distributionAndLogisticsManagementPlatform.service.StockCheckService;
import com.distributionAndLogisticsManagementPlatform.service.StockService;
import com.distributionAndLogisticsManagementPlatform.utils.LogisticUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
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
    private BatchRepository batchRepository;
    @Autowired
    private StockService stockService;
    @Autowired
    private JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> createStockCheck(StockCheckDto stockCheckDto) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(
                        LogisticConstants.UNAUTHORIZED_ACCESS,
                        HttpStatus.UNAUTHORIZED
                );
            }

            if (stockCheckDto.getWarehouseId() == null) {
                return LogisticUtils.getResponseEntity("Warehouse is required", HttpStatus.BAD_REQUEST);
            }

            Optional<Warehouse> warehouseOpt = warehouseRepository.findById(stockCheckDto.getWarehouseId());
            if (warehouseOpt.isEmpty()) {
                return LogisticUtils.getResponseEntity("Warehouse not found", HttpStatus.BAD_REQUEST);
            }

            if (stockCheckDto.getLines() == null || stockCheckDto.getLines().isEmpty()) {
                return LogisticUtils.getResponseEntity("At least one product must be selected", HttpStatus.BAD_REQUEST);
            }

            for (StockCheckCountDto lineDto : stockCheckDto.getLines()) {
                if (lineDto.getProductId() == null) {
                    return LogisticUtils.getResponseEntity("Product is required on every line.", HttpStatus.BAD_REQUEST);
                }
            }

            StockCheck stockCheck = new StockCheck();
            stockCheck.setStockCheckNo("SC" + String.format("%05d", stockCheckRepository.count() + 1));
            stockCheck.setCheckDate(stockCheckDto.getCheckDate() != null ? stockCheckDto.getCheckDate() : LocalDate.now());
            stockCheck.setTaskType(stockCheckDto.getTaskType());
            stockCheck.setWarehouse(warehouseOpt.get());
            stockCheck.setDescription(stockCheckDto.getDescription());
            stockCheck.setStatus("PENDING");
            stockCheck.setCreatedBy(jwtFilter.getCurrentUser());
            stockCheck.setUpdatedBy(jwtFilter.getCurrentUser());

            Long warehouseId = stockCheckDto.getWarehouseId();

            for (StockCheckCountDto lineDto : stockCheckDto.getLines()) {
                Optional<Product> productOpt = productRepository.findById(lineDto.getProductId());
                if (productOpt.isEmpty()) {
                    return LogisticUtils.getResponseEntity("Product not found: " + lineDto.getProductId(), HttpStatus.BAD_REQUEST);
                }

                Product product = productOpt.get();

                // Counted qty from request (can be null if user will count later)
                BigDecimal countedFromRequest = lineDto.getCountedQuantity();

                // Line notes from request
                String lineNotes = lineDto.getNotes();

                // Optional explicit batch from request
                String requestedBatchNo = lineDto.getBatchNo();
                boolean hasRequestedBatch = requestedBatchNo != null && !requestedBatchNo.isBlank();

                // Current stock levels for this product in warehouse
                List<StockLevel> levels = stockLevelRepository.findByProductIdAndWarehouseId(product.getId(), warehouseId);

                if (levels == null || levels.isEmpty()) {
                    // No stock exists yet – still create a line with 0
                    StockCheckLine line = new StockCheckLine();
                    line.setProduct(product);
                    line.setBatch(null);
                    line.setSystemQuantity(lineDto.getSystemQuantity() != null ? lineDto.getSystemQuantity() : BigDecimal.ZERO);
                    line.setCountedQuantity(countedFromRequest);
                    if (countedFromRequest != null) {
                        line.setVariance(countedFromRequest.subtract(line.getSystemQuantity()));
                    } else {
                        line.setVariance(null);
                    }
                    line.setNotes(lineNotes);
                    stockCheck.addLine(line);

                } else if (hasRequestedBatch) {
                    // User selected a specific batch – create one line for that batch
                    Optional<Batch> batchOpt = batchRepository.findByLotNumber(requestedBatchNo.trim());
                    Batch batch = batchOpt.orElse(null);

                    BigDecimal systemQty = lineDto.getSystemQuantity();
                    if (systemQty == null) {
                        systemQty = levels.stream()
                                .filter(l -> l.getBatch() != null
                                        && requestedBatchNo.trim().equalsIgnoreCase(
                                        l.getBatch().getLotNumber()))
                                .map(StockLevel::getQuantityOnHand)
                                .filter(Objects::nonNull)
                                .findFirst()
                                .orElse(BigDecimal.ZERO);
                    }

                    StockCheckLine line = new StockCheckLine();
                    line.setProduct(product);
                    line.setBatch(batch);
                    line.setSystemQuantity(systemQty);
                    line.setCountedQuantity(countedFromRequest);
                    if (countedFromRequest != null) {
                        line.setVariance(countedFromRequest.subtract(systemQty));
                    } else {
                        line.setVariance(null);
                    }
                    line.setNotes(lineNotes);
                    stockCheck.addLine(line);

                } else {
                    // No batch specified – expand to all stock levels (batch rows)
                    // Same behaviour as your original production logic
                    for (StockLevel level : levels) {
                        StockCheckLine line = new StockCheckLine();
                        line.setProduct(product);
                        line.setBatch(level.getBatch());
                        line.setSystemQuantity(level.getQuantityOnHand() != null ? level.getQuantityOnHand() : BigDecimal.ZERO);

                        // If frontend sent one countedQty for product, apply only when single level;
                        // for multiple levels leave null so user counts per batch, unless systemQty matched request
                        if (levels.size() == 1 && countedFromRequest != null) {
                            line.setCountedQuantity(countedFromRequest);
                            line.setVariance(countedFromRequest.subtract(line.getSystemQuantity()));
                        } else if (lineDto.getSystemQuantity() != null && countedFromRequest != null && levels.size() == 1) {
                            line.setCountedQuantity(countedFromRequest);
                            line.setVariance(countedFromRequest.subtract(line.getSystemQuantity()));
                        } else {
                            // Prefer request counted qty when provided even for multi-level if same product line
                            // Production choice: apply counted only when single level; else null
                            line.setCountedQuantity(levels.size() == 1 ? countedFromRequest : null);
                            if (line.getCountedQuantity() != null) {
                                line.setVariance(
                                        line.getCountedQuantity().subtract(line.getSystemQuantity())
                                );
                            } else {
                                line.setVariance(null);
                            }
                        }

                        line.setNotes(lineNotes);
                        stockCheck.addLine(line);
                    }
                }
            }

            if (stockCheck.getLines() == null || stockCheck.getLines().isEmpty()) {
                return LogisticUtils.getResponseEntity(
                        "No stock check lines could be created",
                        HttpStatus.BAD_REQUEST
                );
            }

            stockCheckRepository.save(stockCheck);

            return LogisticUtils.getResponseEntity("Stock Check created successfully: " + stockCheck.getStockCheckNo(), HttpStatus.CREATED);

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
    @Transactional
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
    @Transactional
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
    @Transactional
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

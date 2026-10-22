package com.distributionAndLogisticsManagementPlatform.serviceImpl;

import com.distributionAndLogisticsManagementPlatform.constants.LogisticConstants;
import com.distributionAndLogisticsManagementPlatform.dto.StockTransferDto;
import com.distributionAndLogisticsManagementPlatform.dto.StockTransferLineDto;
import com.distributionAndLogisticsManagementPlatform.entity.Product;
import com.distributionAndLogisticsManagementPlatform.entity.StockTransfer;
import com.distributionAndLogisticsManagementPlatform.entity.StockTransferLine;
import com.distributionAndLogisticsManagementPlatform.entity.Warehouse;
import com.distributionAndLogisticsManagementPlatform.repository.ProductRepository;
import com.distributionAndLogisticsManagementPlatform.repository.StockTransferRepository;
import com.distributionAndLogisticsManagementPlatform.repository.WarehouseRepository;
import com.distributionAndLogisticsManagementPlatform.security.JwtFilter;
import com.distributionAndLogisticsManagementPlatform.service.StockTransferService;
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
import java.util.Optional;

//Add your annotations here
@Service
public class StockTransferServiceImpl implements StockTransferService {

    @Autowired
    private StockTransferRepository stockTransferRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private JwtFilter jwtFilter;

    @Override
    @Transactional
    public ResponseEntity<String> createStockTransfer(StockTransferDto stockTransferDto) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

            if (stockTransferDto.getSourceWarehouseId() == null || stockTransferDto.getDestinationWarehouseId() == null) {
                return LogisticUtils.getResponseEntity("Source and Destination warehouse are required", HttpStatus.BAD_REQUEST);
            }
            if (stockTransferDto.getSourceWarehouseId().equals(stockTransferDto.getDestinationWarehouseId())) {
                return LogisticUtils.getResponseEntity("Source and Destination warehouse cannot be the same", HttpStatus.BAD_REQUEST);
            }

            if (stockTransferDto.getLines() == null || stockTransferDto.getLines().isEmpty()) {
                return LogisticUtils.getResponseEntity("At least one line item is required", HttpStatus.BAD_REQUEST);
            }

            Optional<Warehouse> sourceOpt = warehouseRepository.findById(stockTransferDto.getSourceWarehouseId());
            Optional<Warehouse> destOpt = warehouseRepository.findById(stockTransferDto.getDestinationWarehouseId());
            if (sourceOpt.isEmpty()) {
                return LogisticUtils.getResponseEntity("Source warehouse not found", HttpStatus.BAD_REQUEST);
            }
            if (destOpt.isEmpty()) {
                return LogisticUtils.getResponseEntity("Destination warehouse not found", HttpStatus.BAD_REQUEST);
            }

            StockTransfer transfer = new StockTransfer();
            transfer.setTransferNo("TS" + String.format("%05d", stockTransferRepository.count() + 1));
            transfer.setDate(stockTransferDto.getDate() != null ? stockTransferDto.getDate() : LocalDate.now());
            transfer.setSourceWarehouse(sourceOpt.get());
            transfer.setDestinationWarehouse(destOpt.get());
            transfer.setTransferStatus("DRAFT");
            transfer.setCreatedBy(jwtFilter.getCurrentUser());
            transfer.setUpdatedBy(jwtFilter.getCurrentUser());

            BigDecimal totalQuantity = BigDecimal.ZERO;

            for (StockTransferLineDto lineDto : stockTransferDto.getLines()) {
                Optional<Product> productOpt = productRepository.findById(lineDto.getProductId());

                if (productOpt.isEmpty()) {
                    return LogisticUtils.getResponseEntity("Product not found: ID " + lineDto.getProductId(), HttpStatus.BAD_REQUEST);
                }

                if (lineDto.getQuantity() == null || lineDto.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                    return LogisticUtils.getResponseEntity("Quantity must be greater than zero", HttpStatus.BAD_REQUEST);
                }

                StockTransferLine line = new StockTransferLine();
                line.setProduct(productOpt.get());
                line.setQuantity(lineDto.getQuantity());
                line.setBatchNo(lineDto.getBatchNo());
                line.setNotes(lineDto.getNotes());

                totalQuantity = totalQuantity.add(lineDto.getQuantity());
                transfer.addLine(line);
            }

            transfer.setTotalQuantity(totalQuantity);
            stockTransferRepository.save(transfer);

            return LogisticUtils.getResponseEntity("Stock Transfer created successfully: " + transfer.getTransferNo(), HttpStatus.CREATED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getAllStockTransfers() {
        try {
            List<StockTransfer> stockTransfers = stockTransferRepository.findAll();
            return LogisticUtils.getResponseEntity("Stock Transfers retrieved successfully", stockTransfers, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getStockTransferById(Long id) {
        try {
            Optional<StockTransfer> transfer = stockTransferRepository.findById(id);
            if (transfer.isEmpty()) {
                return LogisticUtils.getResponseEntity("Stock Transfer not found", HttpStatus.NOT_FOUND);
            }
            return LogisticUtils.getResponseEntity("Stock Transfer retrieved successfully", transfer.get(), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getStockTransfersByStatus(String transferStatus) {
        try {
            if (!transferStatus.trim().equalsIgnoreCase("IN_TRANSIT") && !transferStatus.trim().equalsIgnoreCase("APPROVED") && !transferStatus.trim().equalsIgnoreCase("RECEIVED") && !transferStatus.trim().equalsIgnoreCase("CANCELLED") && !transferStatus.trim().equalsIgnoreCase("DRAFT")) {
                return LogisticUtils.getResponseEntity("Invalid goods received note status", HttpStatus.BAD_REQUEST);
            }
            List<StockTransfer> stockTransfers = stockTransferRepository.findByTransferStatus(transferStatus);

            return LogisticUtils.getResponseEntity("Stock Transfers retrieved successfully", stockTransfers, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> approveStockTransfer(Long id) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
            Optional<StockTransfer> stockTransfer = stockTransferRepository.findById(id);
            if (stockTransfer.isEmpty()) {
                return LogisticUtils.getResponseEntity("Stock Transfer not found", HttpStatus.NOT_FOUND);
            }
            StockTransfer transfer = stockTransfer.get();

            if (!"DRAFT".equals(transfer.getTransferStatus())) {
                return LogisticUtils.getResponseEntity("Only DRAFT transfers can be approved", HttpStatus.BAD_REQUEST);
            }

            transfer.setTransferStatus("APPROVED");
            // In real system we would decrease stock from source warehouse here
            transfer.setUpdatedBy(jwtFilter.getCurrentUser());
            transfer.setUpdatedAt(Instant.now());

            stockTransferRepository.save(transfer);

            return LogisticUtils.getResponseEntity("Stock Transfer approved successfully", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> receiveStockTransfer(Long id) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
            Optional<StockTransfer> optional = stockTransferRepository.findById(id);
            if (optional.isEmpty()) {
                return LogisticUtils.getResponseEntity("Stock Transfer not found", HttpStatus.NOT_FOUND);
            }
            StockTransfer transfer = optional.get();
            if (!"APPROVED".equals(transfer.getTransferStatus()) && !"IN_TRANSIT".equals(transfer.getTransferStatus())) {
                return LogisticUtils.getResponseEntity("Only approved or in transit transfers can be received", HttpStatus.BAD_REQUEST);
            }

            transfer.setTransferStatus("RECEIVED");
            // In real system we would increase stock in destination warehouse here
            transfer.setUpdatedBy(jwtFilter.getCurrentUser());
            transfer.setUpdatedAt(Instant.now());

            stockTransferRepository.save(transfer);

            return LogisticUtils.getResponseEntity("Stock Transfer received successfully", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> cancelStockTransfer(Long id) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
            Optional<StockTransfer> stockTransfer = stockTransferRepository.findById(id);
            if (stockTransfer.isEmpty()) {
                return LogisticUtils.getResponseEntity("Stock Transfer not found", HttpStatus.NOT_FOUND);
            }
            StockTransfer transfer = stockTransfer.get();

            if (!"RECEIVED".equals(transfer.getTransferStatus())) {
                return LogisticUtils.getResponseEntity("Cannot cancel a received transfer", HttpStatus.BAD_REQUEST);
            }

            transfer.setTransferStatus("CANCELLED");
            transfer.setUpdatedBy(jwtFilter.getCurrentUser());
            transfer.setUpdatedAt(Instant.now());
            stockTransferRepository.save(transfer);

            return LogisticUtils.getResponseEntity("Stock Transfer cancelled successfully", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

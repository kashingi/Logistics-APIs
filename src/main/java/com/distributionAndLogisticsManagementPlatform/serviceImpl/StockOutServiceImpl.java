package com.distributionAndLogisticsManagementPlatform.serviceImpl;

import com.distributionAndLogisticsManagementPlatform.constants.LogisticConstants;
import com.distributionAndLogisticsManagementPlatform.dto.StockOutDto;
import com.distributionAndLogisticsManagementPlatform.dto.StockOutLineDto;
import com.distributionAndLogisticsManagementPlatform.entity.Product;
import com.distributionAndLogisticsManagementPlatform.entity.StockOut;
import com.distributionAndLogisticsManagementPlatform.entity.StockOutLine;
import com.distributionAndLogisticsManagementPlatform.entity.Warehouse;
import com.distributionAndLogisticsManagementPlatform.repository.ProductRepository;
import com.distributionAndLogisticsManagementPlatform.repository.StockOutRepository;
import com.distributionAndLogisticsManagementPlatform.repository.WarehouseRepository;
import com.distributionAndLogisticsManagementPlatform.security.JwtFilter;
import com.distributionAndLogisticsManagementPlatform.service.StockOutService;
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
public class StockOutServiceImpl implements StockOutService {

    @Autowired
    private StockOutRepository stockOutRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private JwtFilter jwtFilter;


    @Override
    public ResponseEntity<String> createStockOut(StockOutDto stockOutDto) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

            if (stockOutDto.getStockOutReason() == null) {
                return LogisticUtils.getResponseEntity("Stock out reason is required", HttpStatus.BAD_REQUEST);
            }

            if (stockOutDto.getLines() == null || stockOutDto.getLines().isEmpty()) {
                return LogisticUtils.getResponseEntity("At least one line item is required", HttpStatus.BAD_REQUEST);
            }

            StockOut stockOut = new StockOut();
            stockOut.setStockOutNo("OTBST" + String.format("%05d", stockOutRepository.count() + 1));
            stockOut.setDate(stockOutDto.getDate() != null ? stockOutDto.getDate() : LocalDate.now());
            stockOut.setStockOutReason(stockOutDto.getStockOutReason());
            stockOut.setNotes(stockOutDto.getNotes());
            stockOut.setApprovalStatus("PENDING");
            stockOut.setCreatedBy(jwtFilter.getCurrentUser());
            stockOut.setUpdatedBy(jwtFilter.getCurrentUser());

            for (StockOutLineDto lineDto : stockOutDto.getLines()) {

                Optional<Product> productOpt = productRepository.findById(lineDto.getProductId());
                Optional<Warehouse> warehouseOpt = warehouseRepository.findById(lineDto.getWarehouseId());

                if (productOpt.isEmpty()) {
                    return LogisticUtils.getResponseEntity("Product not found: ID " + lineDto.getProductId(), HttpStatus.BAD_REQUEST);
                }
                if (warehouseOpt.isEmpty()) {
                    return LogisticUtils.getResponseEntity("Warehouse not found: ID " + lineDto.getWarehouseId(), HttpStatus.BAD_REQUEST);
                }

                if (lineDto.getQuantity() == null || lineDto.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                    return LogisticUtils.getResponseEntity("Quantity must be greater than zero", HttpStatus.BAD_REQUEST);
                }

                StockOutLine line = new StockOutLine();
                line.setProduct(productOpt.get());
                line.setWarehouse(warehouseOpt.get());
                line.setQuantity(lineDto.getQuantity());
                line.setBatchNo(lineDto.getBatchNo());

                stockOut.addLine(line);
            }

            stockOutRepository.save(stockOut);

            return LogisticUtils.getResponseEntity(
                    "Stock Out created successfully: " + stockOut.getStockOutNo(),
                    HttpStatus.CREATED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getAllStockOuts() {
        try {
            List<StockOut> stockOuts = stockOutRepository.findAll();

            return LogisticUtils.getResponseEntity("Stock Outs retrieved successfully", stockOuts, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getStockOutById(Long id) {
        try {
            Optional<StockOut> stockOut = stockOutRepository.findById(id);
            if (stockOut.isEmpty()) {
                return LogisticUtils.getResponseEntity("Stock Out not found", HttpStatus.NOT_FOUND);
            }
            return LogisticUtils.getResponseEntity("Stock Out retrieved successfully", stockOut.get(), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getStockOutsByStatus(String approvalStatus) {
        try {
            List<StockOut> stockOuts = stockOutRepository.findByApprovalStatus(approvalStatus);

            return LogisticUtils.getResponseEntity("Stock Outs retrieved successfully", stockOuts, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> approveStockOut(Long id) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

            Optional<StockOut> optional = stockOutRepository.findById(id);
            if (optional.isEmpty()) {
                return LogisticUtils.getResponseEntity("Stock Out not found", HttpStatus.NOT_FOUND);
            }

            StockOut stockOut = optional.get();
            if (!"PENDING".equals(stockOut.getApprovalStatus())) {
                return LogisticUtils.getResponseEntity("Only PENDING Stock Out can be approved", HttpStatus.BAD_REQUEST);
            }

            stockOut.setApprovalStatus("APPROVED");
            stockOut.setUpdatedBy(jwtFilter.getCurrentUser());
            stockOut.setUpdatedAt(Instant.now());

            // TODO: Decrease actual stock levels here later

            stockOutRepository.save(stockOut);

            return LogisticUtils.getResponseEntity("Stock Out approved successfully", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> rejectStockOut(Long id) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
            Optional<StockOut> optional = stockOutRepository.findById(id);
            if (optional.isEmpty()) {
                return LogisticUtils.getResponseEntity("Stock Out not found", HttpStatus.NOT_FOUND);
            }

            StockOut stockOut = optional.get();
            stockOut.setApprovalStatus("REJECTED");
            stockOut.setUpdatedBy(jwtFilter.getCurrentUser());
            stockOut.setUpdatedAt(Instant.now());
            stockOutRepository.save(stockOut);

            return LogisticUtils.getResponseEntity("Stock Out rejected", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

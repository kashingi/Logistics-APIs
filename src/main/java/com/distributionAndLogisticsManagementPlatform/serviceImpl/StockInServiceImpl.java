package com.distributionAndLogisticsManagementPlatform.serviceImpl;

import com.distributionAndLogisticsManagementPlatform.constants.LogisticConstants;
import com.distributionAndLogisticsManagementPlatform.dto.StockInDto;
import com.distributionAndLogisticsManagementPlatform.dto.StockInLineDto;
import com.distributionAndLogisticsManagementPlatform.entity.*;
import com.distributionAndLogisticsManagementPlatform.repository.ProductRepository;
import com.distributionAndLogisticsManagementPlatform.repository.StockInRepository;
import com.distributionAndLogisticsManagementPlatform.repository.SupplierRepository;
import com.distributionAndLogisticsManagementPlatform.repository.WarehouseRepository;
import com.distributionAndLogisticsManagementPlatform.security.JwtFilter;
import com.distributionAndLogisticsManagementPlatform.service.StockInService;
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
public class StockInServiceImpl implements StockInService {

    @Autowired
    private StockInRepository stockInRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private JwtFilter jwtFilter;


    @Override
    @Transactional
    public ResponseEntity<String> createStockIn(StockInDto stockInDto) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

            if (stockInDto.getLines() == null || stockInDto.getLines().isEmpty()) {
                return LogisticUtils.getResponseEntity("At least one line item is required", HttpStatus.BAD_REQUEST);
            }

            StockIn stockIn = new StockIn();
            stockIn.setStockInNo("IBDST" + String.format("%05d", stockInRepository.count() + 1));
            stockIn.setDate(stockInDto.getDate() != null ? stockInDto.getDate() : LocalDate.now());
            stockIn.setNotes(stockInDto.getNotes());
            stockIn.setApprovalStatus("PENDING");
            stockIn.setCreatedBy(jwtFilter.getCurrentUser());
            stockIn.setUpdatedBy(jwtFilter.getCurrentUser());

            if (stockInDto.getSupplierId() != null) {
                Optional<Supplier> supplierOpt = supplierRepository.findById(stockInDto.getSupplierId());
                if (supplierOpt.isEmpty()) {
                    return LogisticUtils.getResponseEntity("Supplier not found.", HttpStatus.NOT_FOUND);
                }
                stockIn.setSupplier(supplierOpt.get());
            }

            BigDecimal totalExpense = BigDecimal.ZERO;

            for (StockInLineDto lineDto : stockInDto.getLines()) {
                Optional<Product> productOpt = productRepository.findById(lineDto.getProductId());
                Optional<Warehouse> warehouseOpt = warehouseRepository.findById(lineDto.getWarehouseId());

                if (productOpt.isEmpty()) {
                    return LogisticUtils.getResponseEntity("Product not found: " + lineDto.getProductId(), HttpStatus.BAD_REQUEST);
                }
                if (warehouseOpt.isEmpty()) {
                    return LogisticUtils.getResponseEntity("Warehouse not found: " + lineDto.getWarehouseId(), HttpStatus.BAD_REQUEST);
                }

                // Validation from SRS
                if (lineDto.getQuantity() == null || lineDto.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                    return LogisticUtils.getResponseEntity("Quantity must be greater than zero", HttpStatus.BAD_REQUEST);
                }

                StockInLine line = new StockInLine();
                line.setProduct(productOpt.get());
                line.setWarehouse(warehouseOpt.get());
                line.setQuantity(lineDto.getQuantity());
                //line.setBatchNo(lineDto.getBatchNo());
                line.setBatchNo("SIBN" + String.format("%04d", stockInRepository.count() + 1));
                line.setExpense(lineDto.getExpense() != null ? lineDto.getExpense() : BigDecimal.ZERO);

                totalExpense = totalExpense.add(line.getExpense());
                stockIn.addLine(line);
            }

            stockIn.setTotalExpense(totalExpense);
            stockInRepository.save(stockIn);

            return LogisticUtils.getResponseEntity("Stock In created successfully: " + stockIn.getStockInNo(), HttpStatus.CREATED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getAllStockIns() {
        try {

            List<StockIn> stockIns = stockInRepository.findAll();
            return LogisticUtils.getResponseEntity("Stock Ins retrieved successfully",
                    stockIns, HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getStockInById(Long id) {
        try {
            Optional<StockIn> stockIn = stockInRepository.findById(id);
            if (stockIn.isEmpty()) {
                return LogisticUtils.getResponseEntity("Stock In not found", HttpStatus.NOT_FOUND);
            }
            return LogisticUtils.getResponseEntity("Stock In retrieved successfully", stockIn.get(), HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @Transactional
    public ResponseEntity<String> approveStockIn(Long id) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

            Optional<StockIn> optionalStockIn = stockInRepository.findById(id);
            if (optionalStockIn.isEmpty()) {
                return LogisticUtils.getResponseEntity("Stock In not found", HttpStatus.NOT_FOUND);
            }

            StockIn stockIn = optionalStockIn.get();
            if (!"PENDING".equals(stockIn.getApprovalStatus())) {
                return LogisticUtils.getResponseEntity("Only pending Stock In can be approved", HttpStatus.BAD_REQUEST);
            }

            stockIn.setApprovalStatus("APPROVED");
            stockIn.setUpdatedBy(jwtFilter.getCurrentUser());
            stockIn.setUpdatedAt(Instant.now());

            // TODO: Increase actual stock levels + create Batch records here later

            stockInRepository.save(stockIn);

            return LogisticUtils.getResponseEntity("Stock In approved successfully", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> rejectStockIn(Long id) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

            Optional<StockIn> optional = stockInRepository.findById(id);
            if (optional.isEmpty()) {
                return LogisticUtils.getResponseEntity("Stock In not found", HttpStatus.NOT_FOUND);
            }

            StockIn stockIn = optional.get();
            stockIn.setApprovalStatus("REJECTED");
            stockIn.setUpdatedBy(jwtFilter.getCurrentUser());
            stockIn.setUpdatedAt(Instant.now());
            stockInRepository.save(stockIn);

            return LogisticUtils.getResponseEntity("Stock In rejected", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getStockInsByStatus(String approvalStatus) {
        try {
            if (!approvalStatus.trim().equalsIgnoreCase("PENDING") && !approvalStatus.trim().equalsIgnoreCase("APPROVED") && !approvalStatus.trim().equalsIgnoreCase("REJECTED") ) {
                return LogisticUtils.getResponseEntity("Invalid goods received note status", HttpStatus.BAD_REQUEST);
            }
            List<StockIn> stockIns = stockInRepository.findByApprovalStatus(approvalStatus.toUpperCase());

            return LogisticUtils.getResponseEntity("Stock in retrieved successfully", stockIns, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

package com.distributionAndLogisticsManagementPlatform.serviceImpl;

import com.distributionAndLogisticsManagementPlatform.constants.LogisticConstants;
import com.distributionAndLogisticsManagementPlatform.dto.PurchaseReturnDto;
import com.distributionAndLogisticsManagementPlatform.dto.PurchaseReturnLineDto;
import com.distributionAndLogisticsManagementPlatform.entity.*;
import com.distributionAndLogisticsManagementPlatform.enums.ProductStatus;
import com.distributionAndLogisticsManagementPlatform.repository.ProductRepository;
import com.distributionAndLogisticsManagementPlatform.repository.PurchaseReturnRepository;
import com.distributionAndLogisticsManagementPlatform.repository.SupplierRepository;
import com.distributionAndLogisticsManagementPlatform.repository.WarehouseRepository;
import com.distributionAndLogisticsManagementPlatform.security.JwtFilter;
import com.distributionAndLogisticsManagementPlatform.service.PurchaseReturnService;
import com.distributionAndLogisticsManagementPlatform.utils.LogisticUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

//Add your annotations here
@Service
public class PurchaseReturnServiceImpl implements PurchaseReturnService {

    @Autowired
    private PurchaseReturnRepository purchaseReturnRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> createPurchaseReturn(PurchaseReturnDto purchaseReturnDto) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

            Optional<Supplier> supplierOpt = supplierRepository.findById(purchaseReturnDto.getSupplierId());
            if (supplierOpt.isEmpty()) {
                return LogisticUtils.getResponseEntity("Supplier not found", HttpStatus.BAD_REQUEST);
            }

            if (purchaseReturnDto.getLines() == null || purchaseReturnDto.getLines().isEmpty()) {
                return LogisticUtils.getResponseEntity("At least one line item is required", HttpStatus.BAD_REQUEST);
            }

            PurchaseReturn pr = new PurchaseReturn();
            pr.setPrNumber("PR" + String.format("%05d", purchaseReturnRepository.count() + 1));
            pr.setSupplier(supplierOpt.get());
            pr.setReturnDate(purchaseReturnDto.getReturnDate() != null ? purchaseReturnDto.getReturnDate() : LocalDate.now());
            pr.setRemarks(purchaseReturnDto.getRemarks());
            pr.setStatus("PENDING_APPROVAL");
            pr.setInvoiceStatus("NOT_INVOICED");
            pr.setPaymentStatus("NOT_PAID");
            pr.setCreatedBy(jwtFilter.getCurrentUser());
            pr.setUpdatedBy(jwtFilter.getCurrentUser());

            BigDecimal totalAmount = BigDecimal.ZERO;

            for (PurchaseReturnLineDto lineDto : purchaseReturnDto.getLines()) {
                Optional<Product> productOpt = productRepository.findById(lineDto.getProductId());
                Optional<Warehouse> warehouseOpt = warehouseRepository.findById(lineDto.getWarehouseId());

                if (productOpt.isEmpty()) {
                    return LogisticUtils.getResponseEntity("Product not found: " + lineDto.getProductId(), HttpStatus.BAD_REQUEST);
                }
                if (warehouseOpt.isEmpty()) {
                    return LogisticUtils.getResponseEntity("Warehouse not found: " + lineDto.getWarehouseId(), HttpStatus.BAD_REQUEST);
                }

                PurchaseReturnLine line = new PurchaseReturnLine();
                line.setProduct(productOpt.get());
                line.setWarehouse(warehouseOpt.get());
                line.setQuantity(lineDto.getQuantity());
                line.setUnitPrice(lineDto.getUnitPrice());
                line.setDiscount(lineDto.getDiscount() != null ? lineDto.getDiscount() : BigDecimal.ZERO);
                line.setTaxRate(lineDto.getTaxRate() != null ? lineDto.getTaxRate() : BigDecimal.ZERO);

                BigDecimal subTotal = lineDto.getQuantity()
                        .multiply(lineDto.getUnitPrice())
                        .subtract(line.getDiscount());

                BigDecimal taxAmount = calculateTax(subTotal, line.getTaxRate());
                BigDecimal finalAmount = subTotal.add(taxAmount);

                line.setAmount(finalAmount.setScale(2, RoundingMode.HALF_UP));
                totalAmount = totalAmount.add(line.getAmount());

                pr.addLine(line);
            }

            pr.setTotalAmount(totalAmount.setScale(2, RoundingMode.HALF_UP));
            purchaseReturnRepository.save(pr);

            return LogisticUtils.getResponseEntity("Purchase Return created successfully: " + pr.getPrNumber(), HttpStatus.CREATED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private BigDecimal calculateTax(BigDecimal amount, BigDecimal taxRate) {
        if (taxRate == null || taxRate.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(taxRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    @Override
    public ResponseEntity<?> getAllPurchaseReturns() {
        try {

            List<PurchaseReturn> purchaseReturns = purchaseReturnRepository.findAll();

            return LogisticUtils.getResponseEntity("Purchase Returns retrieved successfully",
                    purchaseReturns, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getPurchaseReturnById(Long id) {
        try {
            Optional<PurchaseReturn> purchaseReturn = purchaseReturnRepository.findById(id);
            if (purchaseReturn.isEmpty()) {
                return LogisticUtils.getResponseEntity("Purchase Return not found", HttpStatus.NOT_FOUND);
            }
            return LogisticUtils.getResponseEntity("Purchase Return retrieved successfully", purchaseReturn.get(), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> approvePurchaseReturn(Long id) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

            Optional<PurchaseReturn> optionalPurchaseReturn = purchaseReturnRepository.findById(id);
            if (optionalPurchaseReturn.isEmpty()) {
                return LogisticUtils.getResponseEntity("Purchase Return not found", HttpStatus.NOT_FOUND);
            }

            PurchaseReturn purchaseReturn = optionalPurchaseReturn.get();

            if (!"PENDING_APPROVAL".equals(purchaseReturn.getStatus())) {
                return LogisticUtils.getResponseEntity("Only pending approval returns can be approved", HttpStatus.BAD_REQUEST);
            }

            purchaseReturn.setStatus("APPROVED");
            purchaseReturn.setUpdatedBy(jwtFilter.getCurrentUser());
            purchaseReturn.setUpdatedAt(Instant.now());

            // TODO: Later decrease stock when approved
            purchaseReturnRepository.save(purchaseReturn);

            return LogisticUtils.getResponseEntity("Purchase Return approved successfully", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> rejectPurchaseReturn(Long id) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

            Optional<PurchaseReturn> optionalPurchaseReturn = purchaseReturnRepository.findById(id);
            if (optionalPurchaseReturn.isEmpty()) {
                return LogisticUtils.getResponseEntity("Purchase Return not found", HttpStatus.NOT_FOUND);
            }

            PurchaseReturn purchaseReturn = optionalPurchaseReturn.get();
            purchaseReturn.setStatus("REJECTED");
            purchaseReturn.setUpdatedBy(jwtFilter.getCurrentUser());
            purchaseReturn.setUpdatedAt(Instant.now());
            purchaseReturnRepository.save(purchaseReturn);

            return LogisticUtils.getResponseEntity("Purchase Return rejected", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getPurchaseReturnsByStatus(String status) {
        try {

            if (!status.trim().equalsIgnoreCase("PENDING_APPROVAL") && !status.trim().equalsIgnoreCase("APPROVED") && !status.trim().equalsIgnoreCase("REJECTED")) {
                return LogisticUtils.getResponseEntity("Invalid status", HttpStatus.BAD_REQUEST);
            }
            List<PurchaseReturn> purchaseReturns = purchaseReturnRepository.findByStatus(status.toUpperCase());

            return LogisticUtils.getResponseEntity("Purchase Returns retrieved successfully", purchaseReturns, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

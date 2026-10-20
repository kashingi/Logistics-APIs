package com.distributionAndLogisticsManagementPlatform.serviceImpl;

import com.distributionAndLogisticsManagementPlatform.constants.LogisticConstants;
import com.distributionAndLogisticsManagementPlatform.dto.PurchaseOrderDto;
import com.distributionAndLogisticsManagementPlatform.dto.PurchaseOrderLineDto;
import com.distributionAndLogisticsManagementPlatform.entity.*;
import com.distributionAndLogisticsManagementPlatform.repository.*;
import com.distributionAndLogisticsManagementPlatform.security.JwtFilter;
import com.distributionAndLogisticsManagementPlatform.service.PurchaseOrderService;
import com.distributionAndLogisticsManagementPlatform.utils.LogisticUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

//Add your annotations here
@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private JwtFilter jwtFilter;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Override
    public ResponseEntity<String> createPurchaseOrder(PurchaseOrderDto purchaseOrderDto) {
        try {
            if (jwtFilter.isAdmin()) {

                String email = jwtFilter.getCurrentUser();
                Optional<User> user = userRepository.findByEmail(email);
                if (user.isEmpty()) {
                    return LogisticUtils.getResponseEntity("User not found", HttpStatus.UNAUTHORIZED);
                }
                // Validate supplier
                Optional<Supplier> supplierOpt = supplierRepository.findById(purchaseOrderDto.getSupplierId());
                if (supplierOpt.isEmpty()) {
                    return LogisticUtils.getResponseEntity("Supplier not found", HttpStatus.BAD_REQUEST);
                }

                if (purchaseOrderDto.getLines() == null || purchaseOrderDto.getLines().isEmpty()) {
                    return LogisticUtils.getResponseEntity("At least one line item is required", HttpStatus.BAD_REQUEST);
                }

                PurchaseOrder po = new PurchaseOrder();
                po.setPoNumber("PO" + String.format("%05d", purchaseOrderRepository.count() + 1));
                po.setSupplier(supplierOpt.get());
                po.setOrderDate(purchaseOrderDto.getOrderDate());
                po.setExpectedDeliveryDate(purchaseOrderDto.getExpectedDeliveryDate());
                po.setStatus("PENDING_APPROVAL");
                po.setInvoiceStatus("NOT_INVOICED");
                po.setReceiptStatus("NOT_RECEIVED");
                po.setCreatedBy(jwtFilter.getCurrentUser());
                po.setUpdatedBy(jwtFilter.getCurrentUser());
                po.setCreatedAt(Instant.now());
                po.setUpdatedAt(Instant.now());

                BigDecimal totalAmount = BigDecimal.ZERO;

                for (PurchaseOrderLineDto lineDto : purchaseOrderDto.getLines()) {
                    Optional<Product> productOpt = productRepository.findById(lineDto.getProductId());
                    Optional<Warehouse> warehouseOpt = warehouseRepository.findById(lineDto.getWarehouseId());

                    if (productOpt.isEmpty()) {
                        return LogisticUtils.getResponseEntity("Product not found: ID " + lineDto.getProductId(), HttpStatus.BAD_REQUEST);
                    }
                    if (warehouseOpt.isEmpty()) {
                        return LogisticUtils.getResponseEntity("Warehouse not found: ID " + lineDto.getWarehouseId(), HttpStatus.BAD_REQUEST);
                    }

                    PurchaseOrderLine line = new PurchaseOrderLine();
                    line.setProduct(productOpt.get());
                    line.setWarehouse(warehouseOpt.get());
                    line.setQuantity(lineDto.getQuantity());
                    line.setUnitPrice(lineDto.getUnitPrice());
                    line.setDiscount(lineDto.getDiscount() != null ? lineDto.getDiscount() : BigDecimal.ZERO);
                    line.setTaxRate(lineDto.getTaxRate());

                    // Calculate amount
                    BigDecimal lineTotal = lineDto.getQuantity()
                            .multiply(lineDto.getUnitPrice())
                            .subtract(line.getDiscount());

                    BigDecimal taxAmount = calculateTax(lineTotal, lineDto.getTaxRate());
                    BigDecimal finalAmount = lineTotal.add(taxAmount);

                    line.setAmount(finalAmount.setScale(2, RoundingMode.HALF_UP));
                    totalAmount = totalAmount.add(line.getAmount());

                    po.addLine(line);
                }

                po.setTotalAmount(totalAmount.setScale(2, RoundingMode.HALF_UP));
                purchaseOrderRepository.save(po);

                return LogisticUtils.getResponseEntity("Purchase Order created successfully: " + po.getPoNumber(), HttpStatus.CREATED);
            }
            return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
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
    public ResponseEntity<?> getAllPurchaseOrders() {
        try {
            List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findAll();

            return LogisticUtils.getResponseEntity("Purchase orders retrieved successfully.", purchaseOrders, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getPurchaseOrderById(Long id) {
        try {
            Optional<PurchaseOrder> po = purchaseOrderRepository.findById(id);
            if (po.isEmpty()) {
                return LogisticUtils.getResponseEntity("Purchase Order not found", HttpStatus.NOT_FOUND);
            }
            return LogisticUtils.getResponseEntity("Purchase order retrieved successfully.", po, HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> approvePurchaseOrder(Long id) {
        try {
            if (jwtFilter.isAdmin()) {
                Optional<PurchaseOrder> optional = purchaseOrderRepository.findById(id);
                if (optional.isEmpty()) {
                    return LogisticUtils.getResponseEntity("Purchase Order not found", HttpStatus.NOT_FOUND);
                }

                PurchaseOrder po = optional.get();
                if (!"PENDING_APPROVAL".equals(po.getStatus())) {
                    return LogisticUtils.getResponseEntity("Only pending approval orders can be approved", HttpStatus.BAD_REQUEST);
                }

                po.setStatus("APPROVED");
                po.setUpdatedBy(jwtFilter.getCurrentUser());
                po.setUpdatedAt(Instant.now());
                purchaseOrderRepository.save(po);

                return LogisticUtils.getResponseEntity("Purchase Order approved successfully", HttpStatus.OK);
            }
            return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> rejectPurchaseOrder(Long id) {
        try {
            if (jwtFilter.isAdmin()) {
                Optional<PurchaseOrder> optional = purchaseOrderRepository.findById(id);
                if (optional.isEmpty()) {
                    return LogisticUtils.getResponseEntity("Purchase Order not found", HttpStatus.NOT_FOUND);
                }

                PurchaseOrder po = optional.get();
                po.setStatus("REJECTED");
                po.setUpdatedBy(jwtFilter.getCurrentUser());
                po.setUpdatedAt(Instant.now());
                purchaseOrderRepository.save(po);

                return LogisticUtils.getResponseEntity("Purchase Order rejected", HttpStatus.OK);

            }
            return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

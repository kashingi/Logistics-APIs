package com.distributionAndLogisticsManagementPlatform.serviceImpl;

import com.distributionAndLogisticsManagementPlatform.constants.LogisticConstants;
import com.distributionAndLogisticsManagementPlatform.dto.GoodsReceivedNoteDto;
import com.distributionAndLogisticsManagementPlatform.dto.GoodsReceivedNoteLineDto;
import com.distributionAndLogisticsManagementPlatform.entity.*;
import com.distributionAndLogisticsManagementPlatform.repository.*;
import com.distributionAndLogisticsManagementPlatform.security.JwtFilter;
import com.distributionAndLogisticsManagementPlatform.service.GoodsReceivedNoteService;
import com.distributionAndLogisticsManagementPlatform.utils.LogisticUtils;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class GoodsReceivedNoteServiceImpl implements GoodsReceivedNoteService {

    @Autowired
    private GoodsReceivedNoteRepository grnRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> createGrn(GoodsReceivedNoteDto goodsReceivedNoteDto) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

            Optional<Supplier> supplierOpt = supplierRepository.findById(goodsReceivedNoteDto.getSupplierId());
            if (supplierOpt.isEmpty()) {
                return LogisticUtils.getResponseEntity("Supplier not found", HttpStatus.BAD_REQUEST);
            }

            if (goodsReceivedNoteDto.getLines() == null || goodsReceivedNoteDto.getLines().isEmpty()) {
                return LogisticUtils.getResponseEntity("At least one line item is required", HttpStatus.BAD_REQUEST);
            }

            GoodsReceivedNote grn = new GoodsReceivedNote();
            grn.setGrnNumber("GRN" + String.format("%05d", grnRepository.count() + 1));
            grn.setSupplier(supplierOpt.get());
            grn.setReceivedDate(goodsReceivedNoteDto.getReceivedDate() != null ? goodsReceivedNoteDto.getReceivedDate() : LocalDate.now());
            grn.setFulfilmentType(goodsReceivedNoteDto.getFulfilmentType());
            grn.setStatus("PENDING_APPROVAL");
            grn.setCreatedBy(jwtFilter.getCurrentUser());
            grn.setUpdatedBy(jwtFilter.getCurrentUser());

            // Link to Purchase Order if provided
            if (goodsReceivedNoteDto.getPurchaseOrderId() != null) {
                Optional<PurchaseOrder> poOpt = purchaseOrderRepository.findById(goodsReceivedNoteDto.getPurchaseOrderId());
                if (poOpt.isPresent()) {
                    grn.setPurchaseOrder(poOpt.get());
                }
            }

            BigDecimal totalAmount = BigDecimal.ZERO;

            for (GoodsReceivedNoteLineDto lineDto : goodsReceivedNoteDto.getLines()) {
                Optional<Product> productOpt = productRepository.findById(lineDto.getProductId());
                Optional<Warehouse> warehouseOpt = warehouseRepository.findById(lineDto.getWarehouseId());

                if (productOpt.isEmpty()) {
                    return LogisticUtils.getResponseEntity("Product not found: " + lineDto.getProductId(), HttpStatus.BAD_REQUEST);
                }
                if (warehouseOpt.isEmpty()) {
                    return LogisticUtils.getResponseEntity("Warehouse not found: " + lineDto.getWarehouseId(), HttpStatus.BAD_REQUEST);
                }

                GoodsReceivedNoteLine line = new GoodsReceivedNoteLine();
                line.setProduct(productOpt.get());
                line.setWarehouse(warehouseOpt.get());
                line.setQuantity(lineDto.getQuantity());
                line.setUnitPrice(lineDto.getUnitPrice());
                line.setTaxRate(lineDto.getTaxRate() != null ? lineDto.getTaxRate() : BigDecimal.ZERO);
                //line.setBatchNo(lineDto.getBatchNo());
                line.setBatchNo("BN" + String.format("%04d", grnRepository.count() + 1));

                BigDecimal subTotal = lineDto.getQuantity().multiply(lineDto.getUnitPrice());
                BigDecimal taxAmount = calculateTax(subTotal, line.getTaxRate());
                BigDecimal finalAmount = subTotal.add(taxAmount);

                line.setAmount(finalAmount.setScale(2, RoundingMode.HALF_UP));
                totalAmount = totalAmount.add(line.getAmount());

                grn.addLine(line);
            }

            grn.setTotalAmount(totalAmount.setScale(2, RoundingMode.HALF_UP));
            grnRepository.save(grn);

            return LogisticUtils.getResponseEntity("Goods received note created successfully: " + grn.getGrnNumber(), HttpStatus.CREATED);

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
    public ResponseEntity<?> getAllGrns() {
        try {
            List<GoodsReceivedNote> goodsReceivedNotes = grnRepository.findAll();

            return LogisticUtils.getResponseEntity("Goods received notes retrieved successfully", goodsReceivedNotes, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getGrnById(Long id) {
        try {
            Optional<GoodsReceivedNote> goodsReceivedNote = grnRepository.findById(id);
            if (goodsReceivedNote.isEmpty()) {
                return LogisticUtils.getResponseEntity("Goods received note not found", HttpStatus.NOT_FOUND);
            }
            return LogisticUtils.getResponseEntity("Goods received note retrieved successfully", goodsReceivedNote.get(), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> approveGrn(Long id) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

            Optional<GoodsReceivedNote> optional = grnRepository.findById(id);
            if (optional.isEmpty()) {
                return LogisticUtils.getResponseEntity("Goods received note not found", HttpStatus.NOT_FOUND);
            }

            GoodsReceivedNote grn = optional.get();
            if (!"PENDING_APPROVAL".equals(grn.getStatus())) {
                return LogisticUtils.getResponseEntity("Only pending approval GRNs can be approved", HttpStatus.BAD_REQUEST);
            }

            grn.setStatus("APPROVED");
            grn.setUpdatedBy(jwtFilter.getCurrentUser());
            grn.setUpdatedAt(Instant.now());

            // TODO: Later we will increase StockLevel + create Batch here
            // For now we just approve

            // Update linked Purchase Order receipt status if exists
            if (grn.getPurchaseOrder() != null) {
                PurchaseOrder po = grn.getPurchaseOrder();
                if ("COMPLETE".equals(grn.getFulfilmentType())) {
                    po.setReceiptStatus("RECEIVED");
                } else {
                    po.setReceiptStatus("PARTIALLY_RECEIVED");
                }
                purchaseOrderRepository.save(po);
            }

            grnRepository.save(grn);

            return LogisticUtils.getResponseEntity("GRN approved successfully", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getGrnsByStatus(String status) {
        try {

            if (!status.trim().equalsIgnoreCase("PENDING_APPROVAL") && !status.trim().equalsIgnoreCase("APPROVED") && !status.trim().equalsIgnoreCase("INVOICED") && !status.trim().equalsIgnoreCase("PAID") && !status.trim().equalsIgnoreCase("PARTIAL_RECEIPT")) {
                return LogisticUtils.getResponseEntity("Invalid goods received note status", HttpStatus.BAD_REQUEST);
            }
            List<GoodsReceivedNote> goodsReceivedNotes = grnRepository.findByStatus(status.toUpperCase());

            return LogisticUtils.getResponseEntity("Goods received notes retrieved successfully", goodsReceivedNotes, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

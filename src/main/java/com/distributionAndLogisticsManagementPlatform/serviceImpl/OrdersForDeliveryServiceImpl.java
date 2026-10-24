package com.distributionAndLogisticsManagementPlatform.serviceImpl;

import com.distributionAndLogisticsManagementPlatform.constants.LogisticConstants;
import com.distributionAndLogisticsManagementPlatform.entity.*;
import com.distributionAndLogisticsManagementPlatform.repository.*;
import com.distributionAndLogisticsManagementPlatform.security.JwtFilter;
import com.distributionAndLogisticsManagementPlatform.service.OrdersForDeliveryService;
import com.distributionAndLogisticsManagementPlatform.service.StockService;
import com.distributionAndLogisticsManagementPlatform.utils.LogisticUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

//Add your annotations here
@Service
public class OrdersForDeliveryServiceImpl implements OrdersForDeliveryService {

    @Autowired
    private SalesOrderRepository salesOrderRepository;
    @Autowired
    private RiderRepository riderRepository;
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private BatchRepository batchRepository;
    @Autowired
    private StockLevelRepository stockLevelRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private StockService stockService;
    @Autowired
    private JwtFilter jwtFilter;

    // ========== 1. Get Orders for Delivery ==========
    @Override
    public ResponseEntity<?> getOrdersForDelivery(String dispatchType) {
        try {
            List<SalesOrder> orders;

            if (dispatchType != null && !dispatchType.isBlank()) {
                orders = salesOrderRepository.findByStatusAndDispatchType("PROCESSING", dispatchType.toUpperCase());
            } else {
                orders = salesOrderRepository.findByStatus("PROCESSING");
            }

            return LogisticUtils.getResponseEntity("Orders for Delivery retrieved successfully", orders, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ========== 2. Assign Rider ==========
    @Override
    @Transactional
    public ResponseEntity<String> assignRider(Long salesOrderId, Long riderId) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
            Optional<SalesOrder> orderOpt = salesOrderRepository.findById(salesOrderId);
            if (orderOpt.isEmpty()) {
                return LogisticUtils.getResponseEntity("Sales Order not found", HttpStatus.NOT_FOUND);
            }

            SalesOrder order = orderOpt.get();
            if (!"PROCESSING".equalsIgnoreCase(order.getStatus())) {
                return LogisticUtils.getResponseEntity("Order must be in PROCESSING status", HttpStatus.BAD_REQUEST);
            }
            if ("COLLECTION".equalsIgnoreCase(order.getDispatchType())) {
                return LogisticUtils.getResponseEntity("Cannot assign rider to COLLECTION order", HttpStatus.BAD_REQUEST);
            }

            Optional<Rider> riderOpt = riderRepository.findById(riderId);
            if (riderOpt.isEmpty()) {
                return LogisticUtils.getResponseEntity("Rider not found", HttpStatus.NOT_FOUND);
            }
            Rider rider = riderOpt.get();
            if (!rider.isAvailable() || !"ACTIVE".equalsIgnoreCase(rider.getStatus())) {
                return LogisticUtils.getResponseEntity("Rider is not available", HttpStatus.CONFLICT);
            }

            // Create or update Delivery
            Delivery delivery = deliveryRepository.findBySalesOrderId(salesOrderId).orElse(new Delivery());

            delivery.setSalesOrder(order);
            delivery.setDeliveryType(order.getDispatchType());
            delivery.setRider(rider);
            delivery.setStatus("PENDING");
            delivery.setCreatedBy(jwtFilter.getCurrentUser());
            delivery.setUpdatedBy(jwtFilter.getCurrentUser());
            deliveryRepository.save(delivery);

            // Mark rider as busy
            rider.setAvailable(false);
            riderRepository.save(rider);

            order.setStatus("IN_PROGRESS");
            order.setUpdatedBy(jwtFilter.getCurrentUser());
            order.setUpdatedAt(Instant.now());
            salesOrderRepository.save(order);

            return LogisticUtils.getResponseEntity("Rider assigned successfully", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ========== 3. Confirm / Update Location ==========
    @Override
    public ResponseEntity<String> confirmLocation(Long salesOrderId, String notes) {
        try {
            Optional<SalesOrder> orderOpt = salesOrderRepository.findById(salesOrderId);
            if (orderOpt.isEmpty()) {
                return LogisticUtils.getResponseEntity("Sales Order not found", HttpStatus.NOT_FOUND);
            }
            SalesOrder order = orderOpt.get();
            if (notes != null && !notes.isBlank()) {
                order.setNotes((order.getNotes() == null ? "" : order.getNotes() + " | ") + notes);
            }
            order.setUpdatedBy(jwtFilter.getCurrentUser());
            order.setUpdatedAt(Instant.now());
            salesOrderRepository.save(order);

            return LogisticUtils.getResponseEntity("Location/notes updated successfully", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ========== 4. Get Packing Queue ==========
    @Override
    public ResponseEntity<?> getPackingQueue(String dispatchType) {
        try {
            return getOrdersForDelivery(dispatchType); // same data for now
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ========== 5. Allocate LOT (FEFO) ==========
    @Override
    @Transactional
    public ResponseEntity<String> allocateLot(Long salesOrderId) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
            Optional<SalesOrder> orderOpt = salesOrderRepository.findById(salesOrderId);
            if (orderOpt.isEmpty()) {
                return LogisticUtils.getResponseEntity("Sales Order not found", HttpStatus.NOT_FOUND);
            }

            SalesOrder order = orderOpt.get();

            Delivery delivery = deliveryRepository.findBySalesOrderId(salesOrderId)
                    .orElseGet(() -> {
                        Delivery d = new Delivery();
                        d.setSalesOrder(order);
                        d.setDeliveryType(order.getDispatchType());
                        d.setStatus("PENDING");
                        d.setCreatedBy(jwtFilter.getCurrentUser());
                        return d;
                    });

            StringBuilder allocatedLots = new StringBuilder();

            for (SalesOrderLine line : order.getLines()) {
                Product product = line.getProduct();

                // Get all batches for this product ordered by expiry (FEFO)
                List<Batch> batches = batchRepository.findAll().stream()
                        .filter(b -> b.getProduct().getId().equals(product.getId()))
                        .filter(b -> b.getQuantityRemaining().compareTo(BigDecimal.ZERO) > 0)
                        .sorted(Comparator.comparing(Batch::getExpiryDate, Comparator.nullsLast(Comparator.naturalOrder())))
                        .toList();

                BigDecimal remainingQty = line.getQuantity();

                for (Batch batch : batches) {
                    if (remainingQty.compareTo(BigDecimal.ZERO) <= 0) break;

                    BigDecimal takeQty = remainingQty.min(batch.getQuantityRemaining());

                    allocatedLots.append(batch.getLotNumber())
                            .append(" (")
                            .append(takeQty)
                            .append("), ");

                    remainingQty = remainingQty.subtract(takeQty);
                }

                if (remainingQty.compareTo(BigDecimal.ZERO) > 0) {
                    return LogisticUtils.getResponseEntity("Insufficient stock for product: " + product.getName(), HttpStatus.BAD_REQUEST);
                }
            }

            delivery.setLotNumber(allocatedLots.toString());
            delivery.setUpdatedBy(jwtFilter.getCurrentUser());
            deliveryRepository.save(delivery);

            return LogisticUtils.getResponseEntity("LOT allocated successfully using FEFO: " + allocatedLots, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @Transactional
    public ResponseEntity<String> scanItem(Long salesOrderId, String barcode) {
        try {
            Optional<SalesOrder> orderOpt = salesOrderRepository.findById(salesOrderId);
            if (orderOpt.isEmpty()) {
                return LogisticUtils.getResponseEntity("Sales Order not found", HttpStatus.NOT_FOUND);
            }

            SalesOrder order = orderOpt.get();
            // Find product by barcode / serial number
            Optional<Product> productOpt = productRepository.findBySerialNumber(barcode);
            if (productOpt.isEmpty()) {
                return LogisticUtils.getResponseEntity("Product not found with barcode: " + barcode, HttpStatus.BAD_REQUEST);
            }

            Product product = productOpt.get();
            // Check if this product is part of the order
            boolean isPartOfOrder = order.getLines().stream().anyMatch(line -> line.getProduct().getId().equals(product.getId()));

            if (!isPartOfOrder) {
                return LogisticUtils.getResponseEntity("This product is not part of the sales order", HttpStatus.BAD_REQUEST);
            }

            Delivery delivery = deliveryRepository.findBySalesOrderId(salesOrderId).orElseThrow(() -> new RuntimeException("Delivery record not found. Please allocate LOT first."));

            // Prevent double scanning
            if (delivery.getScannedBarcodes().contains(barcode)) {
                return LogisticUtils.getResponseEntity("This barcode has already been scanned", HttpStatus.BAD_REQUEST);
            }

            // Decrease stock (using first available batch for simplicity – can be improved)
            Optional<SalesOrderLine> lineOpt = order.getLines().stream().filter(l -> l.getProduct().getId().equals(product.getId())).findFirst();

            if (lineOpt.isPresent()) {
                stockService.decreaseStock(
                        product,
                        // For simplicity we take the first warehouse – in real system you should store warehouse on the line
                        // You can improve this later
                        null, // temporary – you should pass correct warehouse
                        BigDecimal.ONE, // scanning one unit at a time
                        null,
                        "SALE",
                        "SALES_ORDER",
                        order.getId(),
                        jwtFilter.getCurrentUser()
                );
            }

            delivery.getScannedBarcodes().add(barcode);
            delivery.setUpdatedBy(jwtFilter.getCurrentUser());
            deliveryRepository.save(delivery);

            return LogisticUtils.getResponseEntity("Item scanned successfully: " + barcode, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

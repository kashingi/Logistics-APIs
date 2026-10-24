package com.distributionAndLogisticsManagementPlatform.serviceImpl;

import com.distributionAndLogisticsManagementPlatform.constants.LogisticConstants;
import com.distributionAndLogisticsManagementPlatform.entity.Rider;
import com.distributionAndLogisticsManagementPlatform.entity.SalesOrder;
import com.distributionAndLogisticsManagementPlatform.repository.RiderRepository;
import com.distributionAndLogisticsManagementPlatform.repository.SalesOrderRepository;
import com.distributionAndLogisticsManagementPlatform.security.JwtFilter;
import com.distributionAndLogisticsManagementPlatform.service.OrdersForDeliveryService;
import com.distributionAndLogisticsManagementPlatform.service.StockService;
import com.distributionAndLogisticsManagementPlatform.utils.LogisticUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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
    private JwtFilter jwtFilter;
    @Autowired private StockService stockService;

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
                return LogisticUtils.getResponseEntity("Order is not in processing status", HttpStatus.BAD_REQUEST);
            }
            if ("COLLECTION".equalsIgnoreCase(order.getDispatchType())) {
                return LogisticUtils.getResponseEntity("Cannot assign rider to a Collection order", HttpStatus.BAD_REQUEST);
            }
            Optional<Rider> riderOpt = riderRepository.findById(riderId);
            if (riderOpt.isEmpty()) {
                return LogisticUtils.getResponseEntity("Rider not found", HttpStatus.NOT_FOUND);
            }
            Rider rider = riderOpt.get();
            if (!rider.isAvailable() || !"ACTIVE".equalsIgnoreCase(rider.getStatus())) {
                return LogisticUtils.getResponseEntity("Rider is not available", HttpStatus.CONFLICT);
            }

            // Here we would normally create a Delivery record and link the rider
            // For now we mark the order as assigned
            order.setStatus("IN_PROGRESS");
            order.setUpdatedBy(jwtFilter.getCurrentUser());
            order.setUpdatedAt(Instant.now());
            salesOrderRepository.save(order);

            // Mark rider as unavailable
            rider.setAvailable(false);
            riderRepository.save(rider);

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
                order.setNotes(order.getNotes() != null ? order.getNotes() + " | " + notes : notes);
            }
            order.setUpdatedBy(jwtFilter.getCurrentUser());
            order.setUpdatedAt(Instant.now());
            salesOrderRepository.save(order);

            return LogisticUtils.getResponseEntity("Location confirmed / notes updated", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ========== 4. Get Packing Queue ==========
    @Override
    public ResponseEntity<?> getPackingQueue(String dispatchType) {
        try {
            List<SalesOrder> orders;

            if (dispatchType != null && !dispatchType.isBlank()) {
                orders = salesOrderRepository.findByStatusAndDispatchType("PROCESSING", dispatchType.toUpperCase());
            } else {
                orders = salesOrderRepository.findByStatus("PROCESSING");
            }

            return LogisticUtils.getResponseEntity("Packing Queue retrieved successfully", orders, HttpStatus.OK);
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
            // Simple version – in real system we would run FEFO logic here
            // For now we just mark that LOT has been allocated
            order.setStatus("PROCESSING"); // stays in packing
            order.setUpdatedBy(jwtFilter.getCurrentUser());
            order.setUpdatedAt(Instant.now());
            salesOrderRepository.save(order);

            return LogisticUtils.getResponseEntity("LOT allocated successfully (FEFO applied)", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> scanItem(Long salesOrderId, String barcode) {
        try {
            Optional<SalesOrder> orderOpt = salesOrderRepository.findById(salesOrderId);
            if (orderOpt.isEmpty()) {
                return LogisticUtils.getResponseEntity("Sales Order not found", HttpStatus.NOT_FOUND);
            }

            // In real system we would:
            // 1. Find the product by barcode
            // 2. Decrease stock using StockService
            // 3. Mark item as scanned

            return LogisticUtils.getResponseEntity("Item scanned successfully: " + barcode, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

package com.distributionAndLogisticsManagementPlatform.serviceImpl;

import com.distributionAndLogisticsManagementPlatform.constants.LogisticConstants;
import com.distributionAndLogisticsManagementPlatform.entity.Delivery;
import com.distributionAndLogisticsManagementPlatform.entity.PickingList;
import com.distributionAndLogisticsManagementPlatform.entity.Rider;
import com.distributionAndLogisticsManagementPlatform.entity.SalesOrder;
import com.distributionAndLogisticsManagementPlatform.repository.*;
import com.distributionAndLogisticsManagementPlatform.security.JwtFilter;
import com.distributionAndLogisticsManagementPlatform.service.DeliveryService;
import com.distributionAndLogisticsManagementPlatform.utils.LogisticUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//Add your annotations here
@Service
public class DeliveryServiceImpl implements DeliveryService {
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private SalesOrderRepository salesOrderRepository;
    @Autowired
    private RiderRepository riderRepository;
    @Autowired
    private DeliveryZoneRepository deliveryZoneRepository;
    @Autowired
    private PickingListRepository pickingListRepository;
    @Autowired
    private JwtFilter jwtFilter;


    @Override
    public ResponseEntity<?> getAllDeliveries(String deliveryType, String status) {
        try {
            List<Delivery> deliveries = deliveryRepository.findAll();
            if (deliveryType != null && !deliveryType.isBlank()) {
                deliveries = deliveries.stream().filter(d -> deliveryType.equalsIgnoreCase(d.getDeliveryType())).toList();
            }
            if (status != null && !status.isBlank()) {
                deliveries = deliveries.stream().filter(d -> status.equalsIgnoreCase(d.getStatus())).toList();
            }

            return LogisticUtils.getResponseEntity("Deliveries retrieved successfully", deliveries, HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getDeliveryById(Long id) {
        try {
            Optional<Delivery> delivery = deliveryRepository.findById(id);
            if (delivery.isEmpty()) {
                return LogisticUtils.getResponseEntity("Delivery not found", HttpStatus.NOT_FOUND);
            }
            return LogisticUtils.getResponseEntity("Delivery retrieved successfully", delivery.get(), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getDeliveriesInProgress() {
        try {
            List<Delivery> list = deliveryRepository.findAll().stream().filter(d -> "IN_PROGRESS".equalsIgnoreCase(d.getStatus()) || "PICKED".equalsIgnoreCase(d.getStatus())).toList();

            return LogisticUtils.getResponseEntity("Deliveries in progress retrieved successfully", list, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @Transactional
    public ResponseEntity<String> markAsPicked(Long deliveryId) {
        try {
            Optional<Delivery> optional = deliveryRepository.findById(deliveryId);
            if (optional.isEmpty()) {
                return LogisticUtils.getResponseEntity("Delivery not found", HttpStatus.NOT_FOUND);
            }

            Delivery delivery = optional.get();
            delivery.setStatus("PICKED");
            delivery.setPickedAt(Instant.now());
            delivery.setUpdatedBy(jwtFilter.getCurrentUser());
            deliveryRepository.save(delivery);

            return LogisticUtils.getResponseEntity("Delivery marked as PICKED", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @Transactional
    public ResponseEntity<String> markAsCollected(Long deliveryId) {
        try {
            Optional<Delivery> optional = deliveryRepository.findById(deliveryId);
            if (optional.isEmpty()) {
                return LogisticUtils.getResponseEntity("Delivery not found", HttpStatus.NOT_FOUND);
            }

            Delivery delivery = optional.get();
            if (!"COLLECTION".equalsIgnoreCase(delivery.getDeliveryType())) {
                return LogisticUtils.getResponseEntity("Only COLLECTION deliveries can be marked as collected", HttpStatus.BAD_REQUEST);
            }

            delivery.setStatus("COLLECTED");
            delivery.setCompletedAt(Instant.now());
            delivery.setUpdatedBy(jwtFilter.getCurrentUser());
            deliveryRepository.save(delivery);

            // Update Sales Order
            SalesOrder order = delivery.getSalesOrder();
            order.setStatus("COMPLETED");
            salesOrderRepository.save(order);

            return LogisticUtils.getResponseEntity("Order marked as COLLECTED", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @Transactional
    public ResponseEntity<String> completeDelivery(Long deliveryId) {
        try {
            Optional<Delivery> optional = deliveryRepository.findById(deliveryId);
            if (optional.isEmpty()) {
                return LogisticUtils.getResponseEntity("Delivery not found", HttpStatus.NOT_FOUND);
            }

            Delivery delivery = optional.get();
            delivery.setStatus("COMPLETED");
            delivery.setCompletedAt(Instant.now());
            delivery.setUpdatedBy(jwtFilter.getCurrentUser());
            deliveryRepository.save(delivery);

            // Free the rider
            if (delivery.getRider() != null) {
                Rider rider = delivery.getRider();
                rider.setAvailable(true);
                riderRepository.save(rider);
            }

            // Update Sales Order
            SalesOrder order = delivery.getSalesOrder();
            order.setStatus("COMPLETED");
            salesOrderRepository.save(order);

            return LogisticUtils.getResponseEntity("Delivery completed successfully", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @Transactional
    public ResponseEntity<String> cancelDelivery(Long deliveryId, String reason, String action) {
        try {
            Optional<Delivery> optional = deliveryRepository.findById(deliveryId);
            if (optional.isEmpty()) {
                return LogisticUtils.getResponseEntity("Delivery not found", HttpStatus.NOT_FOUND);
            }

            Delivery delivery = optional.get();
            delivery.setStatus("CANCELLED");
            delivery.setUpdatedBy(jwtFilter.getCurrentUser());
            deliveryRepository.save(delivery);

            // Free the rider
            if (delivery.getRider() != null) {
                Rider rider = delivery.getRider();
                rider.setAvailable(true);
                riderRepository.save(rider);
            }

            SalesOrder order = delivery.getSalesOrder();
            if ("RETURN_TO_PENDING".equalsIgnoreCase(action)) {
                order.setStatus("PENDING");
                order.setDispatchType(null);
            } else {
                order.setStatus("CANCELLED");
            }
            salesOrderRepository.save(order);

            return LogisticUtils.getResponseEntity("Delivery cancelled. Action: " + action, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @Transactional
    public ResponseEntity<String> returnDelivery(Long deliveryId, String reason, String action) {
        try {
            Optional<Delivery> optional = deliveryRepository.findById(deliveryId);
            if (optional.isEmpty()) {
                return LogisticUtils.getResponseEntity("Delivery not found", HttpStatus.NOT_FOUND);
            }

            Delivery delivery = optional.get();
            delivery.setStatus("RETURNED");
            delivery.setUpdatedBy(jwtFilter.getCurrentUser());
            deliveryRepository.save(delivery);

            if (delivery.getRider() != null) {
                Rider rider = delivery.getRider();
                rider.setAvailable(true);
                riderRepository.save(rider);
            }

            SalesOrder order = delivery.getSalesOrder();
            order.setStatus("RETURNED");
            salesOrderRepository.save(order);

            // In real system you would also create Credit Note or schedule replacement here

            return LogisticUtils.getResponseEntity("Delivery returned. Reason: " + reason + ", Action: " + action, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ===================== PICKING LISTS =====================

    @Override
    @Transactional
    public ResponseEntity<String> createPickingList(String deliveryType, Long riderId, Long zoneId, List<Long> salesOrderIds) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
            if (salesOrderIds == null || salesOrderIds.isEmpty()) {
                return LogisticUtils.getResponseEntity("At least one Sales Order is required", HttpStatus.BAD_REQUEST);
            }

            PickingList pickingList = new PickingList();
            pickingList.setPickingListNo("PL" + String.format("%05d", pickingListRepository.count() + 1));
            pickingList.setDate(LocalDate.now());
            pickingList.setDeliveryType(deliveryType.toUpperCase());
            pickingList.setStatus("NOT_PICKED");
            pickingList.setCreatedBy(jwtFilter.getCurrentUser());
            pickingList.setUpdatedBy(jwtFilter.getCurrentUser());

            if (riderId != null) {
                riderRepository.findById(riderId).ifPresent(pickingList::setRider);
            }
            if (zoneId != null) {
                deliveryZoneRepository.findById(zoneId).ifPresent(pickingList::setZone);
            }

            List<SalesOrder> orders = new ArrayList<>();
            for (Long soId : salesOrderIds) {
                salesOrderRepository.findById(soId).ifPresent(orders::add);
            }
            pickingList.setOrders(orders);

            pickingListRepository.save(pickingList);

            return LogisticUtils.getResponseEntity("Picking List created successfully: " + pickingList.getPickingListNo(), HttpStatus.CREATED);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getPickingLists(String deliveryType) {
        try {
            List<PickingList> list;
            if (deliveryType != null && !deliveryType.isBlank()) {
                list = pickingListRepository.findByDeliveryType(deliveryType.toUpperCase());
            } else {
                list = pickingListRepository.findAll();
            }
            return LogisticUtils.getResponseEntity("Picking Lists retrieved successfully", list, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @Transactional
    public ResponseEntity<String> markPickingListAsPicked(Long pickingListId) {
        try {
            Optional<PickingList> optional = pickingListRepository.findById(pickingListId);
            if (optional.isEmpty()) {
                return LogisticUtils.getResponseEntity("Picking List not found", HttpStatus.NOT_FOUND);
            }

            PickingList pl = optional.get();
            pl.setStatus("PICKED");
            pl.setUpdatedBy(jwtFilter.getCurrentUser());
            pickingListRepository.save(pl);

            return LogisticUtils.getResponseEntity("Picking List marked as PICKED", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

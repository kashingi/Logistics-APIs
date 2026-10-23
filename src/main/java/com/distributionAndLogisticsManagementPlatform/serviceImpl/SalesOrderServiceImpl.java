package com.distributionAndLogisticsManagementPlatform.serviceImpl;

import com.distributionAndLogisticsManagementPlatform.constants.LogisticConstants;
import com.distributionAndLogisticsManagementPlatform.dto.SalesOrderDto;
import com.distributionAndLogisticsManagementPlatform.dto.SalesOrderLineDto;
import com.distributionAndLogisticsManagementPlatform.entity.*;
import com.distributionAndLogisticsManagementPlatform.repository.*;
import com.distributionAndLogisticsManagementPlatform.security.JwtFilter;
import com.distributionAndLogisticsManagementPlatform.service.SalesOrderService;
import com.distributionAndLogisticsManagementPlatform.utils.LogisticUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

//add your annotations here
@Service
public class SalesOrderServiceImpl implements SalesOrderService {

    @Autowired
    private SalesOrderRepository salesOrderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SalesQuotationRepository salesQuotationRepository;
    @Autowired private JwtFilter jwtFilter;
    @Autowired
    private CreditNoteRepository creditNoteRepository;

    @Override
    @Transactional
    public ResponseEntity<String> createSalesOrder(SalesOrderDto salesOrderDto) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

            if (salesOrderDto.getLines() == null || salesOrderDto.getLines().isEmpty()) {
                return LogisticUtils.getResponseEntity("At least one line item is required", HttpStatus.BAD_REQUEST);
            }

            // Handle Customer (same logic as Quotation)
            Customer customer = resolveCustomer(salesOrderDto);

            SalesOrder order = new SalesOrder();
            order.setSoNumber("SO" + String.format("%05d", salesOrderRepository.count() + 1));
            order.setDate(salesOrderDto.getDate() != null ? salesOrderDto.getDate() : LocalDate.now());
            order.setCustomer(customer);
            order.setKraPin(salesOrderDto.getKraPin());
            order.setSalespersonName(salesOrderDto.getSalespersonName());
            order.setSalespersonCode(salesOrderDto.getSalespersonCode());
            order.setOrigin("INTERNAL");
            order.setStatus("DRAFT");
            order.setNotes(salesOrderDto.getNotes());
            order.setTerms(salesOrderDto.getTerms());
            order.setBuildingLandmark(salesOrderDto.getBuildingLandmark());
            order.setStreet(salesOrderDto.getStreet());
            order.setHouseNo(salesOrderDto.getHouseNo());
            order.setContactPerson(salesOrderDto.getContactPerson());
            order.setContactPhone(salesOrderDto.getContactPhone());
            order.setCreatedBy(jwtFilter.getCurrentUser());
            order.setUpdatedBy(jwtFilter.getCurrentUser());

            // Link to quotation if provided
            if (salesOrderDto.getSourceQuotationId() != null) {
                Optional<SalesQuotation> quotationOpt = salesQuotationRepository.findById(salesOrderDto.getSourceQuotationId());
                if (quotationOpt.isPresent()) {
                    order.setSourceQuotation(quotationOpt.get());
                    quotationOpt.get().setStatus("CONVERTED");
                    salesQuotationRepository.save(quotationOpt.get());
                }
            }

            BigDecimal totalAmount = BigDecimal.ZERO;

            for (SalesOrderLineDto lineDto : salesOrderDto.getLines()) {
                Optional<Product> productOpt = productRepository.findById(lineDto.getProductId());
                if (productOpt.isEmpty()) {
                    return LogisticUtils.getResponseEntity("Product not found: " + lineDto.getProductId(), HttpStatus.BAD_REQUEST);
                }

                Product product = productOpt.get();

                SalesOrderLine line = new SalesOrderLine();
                line.setProduct(product);
                line.setQuantity(lineDto.getQuantity());
                line.setUom(product.getUom() != null ? product.getUom().name() : null);
                line.setPriceTier(lineDto.getPriceTier() != null ? lineDto.getPriceTier() : "RETAIL");
                line.setDiscount(lineDto.getDiscount() != null ? lineDto.getDiscount() : BigDecimal.ZERO);
                line.setFulfilment(lineDto.getFulfilment() != null ? lineDto.getFulfilment() : "DELIVERY");

                BigDecimal unitPrice = ("WHOLESALE".equals(line.getPriceTier()) && product.getWholesalePrice() != null)
                        ? product.getWholesalePrice()
                        : (product.getRetailPrice() != null ? product.getRetailPrice() : BigDecimal.ZERO);

                line.setUnitPrice(unitPrice);

                BigDecimal lineTotal = lineDto.getQuantity().multiply(unitPrice).subtract(line.getDiscount());
                line.setAmount(lineTotal.setScale(2, RoundingMode.HALF_UP));

                totalAmount = totalAmount.add(line.getAmount());
                order.addLine(line);
            }

            order.setTotalAmount(totalAmount.setScale(2, RoundingMode.HALF_UP));
            salesOrderRepository.save(order);

            return LogisticUtils.getResponseEntity(
                    "Sales Order created successfully: " + order.getSoNumber(),
                    HttpStatus.CREATED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Customer resolveCustomer(SalesOrderDto dto) {
        if (dto.getCustomerId() != null) {
            return customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
        }

        Customer customer = new Customer();
        customer.setName(dto.getCustomerName() != null ? dto.getCustomerName() : dto.getFirstName() + " " + dto.getLastName());
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setPhone(dto.getPhone());
        customer.setEmail(dto.getEmail());
        customer.setKraPin(dto.getKraPin());
        customer.setCreatedBy(jwtFilter.getCurrentUser());
        return customerRepository.save(customer);
    }

    @Override
    public ResponseEntity<?> getAllSalesOrders() {
        try {
            List<SalesOrder> salesOrders = salesOrderRepository.findAll();

            return LogisticUtils.getResponseEntity("Sales Orders retrieved successfully", salesOrders, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getSalesOrderById(Long id) {
        try {
            Optional<SalesOrder> salesOrder = salesOrderRepository.findById(id);
            if (salesOrder.isEmpty()) {
                return LogisticUtils.getResponseEntity("Sales Order not found", HttpStatus.NOT_FOUND);
            }
            return LogisticUtils.getResponseEntity("Sales Order retrieved successfully", salesOrder.get(), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getSalesOrdersByStatus(String status) {
        try {

            if (!status.trim().equalsIgnoreCase("DRAFT") && !status.trim().equalsIgnoreCase("PENDING") && !status.trim().equalsIgnoreCase("PROCESSING") && !status.trim().equalsIgnoreCase("IN_PROGRESS") && !status.trim().equalsIgnoreCase("COMPLETED") && !status.trim().equalsIgnoreCase("CANCELLED") && !status.trim().equalsIgnoreCase("RETURNED")) {
                return LogisticUtils.getResponseEntity("Invalid sales order status", HttpStatus.BAD_REQUEST);
            }
            List<SalesOrder> salesOrderList = salesOrderRepository.findByStatus(status.toUpperCase());

            return LogisticUtils.getResponseEntity("Sales Orders retrieved successfully", salesOrderList, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @Transactional
    public ResponseEntity<String> confirmSalesOrder(Long id) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
            Optional<SalesOrder> optional = salesOrderRepository.findById(id);
            if (optional.isEmpty()) {
                return LogisticUtils.getResponseEntity("Sales Order not found", HttpStatus.NOT_FOUND);
            }

            SalesOrder order = optional.get();
            if (!"DRAFT".equalsIgnoreCase(order.getStatus())) {
                return LogisticUtils.getResponseEntity("Only draft orders can be confirmed", HttpStatus.BAD_REQUEST);
            }

            // Later we can reserve stock here
            order.setStatus("PENDING");
            order.setUpdatedBy(jwtFilter.getCurrentUser());
            order.setUpdatedAt(Instant.now());
            salesOrderRepository.save(order);

            return LogisticUtils.getResponseEntity("Sales Order confirmed and moved to PENDING", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> cancelSalesOrder(Long id) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
            Optional<SalesOrder> optionalSalesOrder = salesOrderRepository.findById(id);
            if (optionalSalesOrder.isEmpty()) {
                return LogisticUtils.getResponseEntity("Sales Order not found", HttpStatus.NOT_FOUND);
            }

            SalesOrder order = optionalSalesOrder.get();
            order.setStatus("CANCELLED");
            order.setUpdatedBy(jwtFilter.getCurrentUser());
            order.setUpdatedAt(Instant.now());
            salesOrderRepository.save(order);

            return LogisticUtils.getResponseEntity("Sales Order cancelled successfully", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getPendingSalesOrders() {
        try {
            List<SalesOrder> pendingOrders = salesOrderRepository.findByStatus("PENDING");

            return LogisticUtils.getResponseEntity("Pending Sales Orders retrieved successfully", pendingOrders, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @Transactional
    public ResponseEntity<String> moveToDispatch(Long id, String dispatchType) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
            Optional<SalesOrder> optional = salesOrderRepository.findById(id);
            if (optional.isEmpty()) {
                return LogisticUtils.getResponseEntity("Sales Order not found", HttpStatus.NOT_FOUND);
            }
            SalesOrder order = optional.get();
            if (!"PENDING".equalsIgnoreCase(order.getStatus())) {
                return LogisticUtils.getResponseEntity("Only pending orders can be moved to dispatch", HttpStatus.BAD_REQUEST);
            }

            String type = dispatchType.toUpperCase().trim();
            if (!type.equals("STANDARD") && !type.equals("EXPRESS")) {
                return LogisticUtils.getResponseEntity("Invalid dispatch type. Use STANDARD or EXPRESS", HttpStatus.BAD_REQUEST);
            }

            order.setDispatchType(type);
            order.setStatus("PROCESSING");
            order.setUpdatedBy(jwtFilter.getCurrentUser());
            order.setUpdatedAt(Instant.now());
            salesOrderRepository.save(order);

            return LogisticUtils.getResponseEntity("Sales Order moved to " + type + " dispatch successfully", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @Transactional
    public ResponseEntity<String> moveToCollection(Long id) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
            Optional<SalesOrder> salesOrder = salesOrderRepository.findById(id);
            if (salesOrder.isEmpty()) {
                return LogisticUtils.getResponseEntity("Sales Order not found", HttpStatus.NOT_FOUND);
            }
            SalesOrder order = salesOrder.get();
            if (!"PENDING".equalsIgnoreCase(order.getStatus())) {
                return LogisticUtils.getResponseEntity("Only pending orders can be moved to collection", HttpStatus.BAD_REQUEST);
            }

            order.setDispatchType("COLLECTION");
            order.setStatus("PROCESSING");
            order.setUpdatedBy(jwtFilter.getCurrentUser());
            order.setUpdatedAt(Instant.now());
            salesOrderRepository.save(order);

            return LogisticUtils.getResponseEntity("Sales Order moved to Customer Collection successfully", HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> cancelWithCreditNote(Long id, String reason) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
            Optional<SalesOrder> salesOrder = salesOrderRepository.findById(id);
            if (salesOrder.isEmpty()) {
                return LogisticUtils.getResponseEntity("Sales Order not found", HttpStatus.NOT_FOUND);
            }
            SalesOrder order = salesOrder.get();
            if ("CANCELLED".equalsIgnoreCase(order.getStatus()) || "COMPLETED".equalsIgnoreCase(order.getStatus())) {
                return LogisticUtils.getResponseEntity("Cannot cancel this order", HttpStatus.BAD_REQUEST);
            }

            // Create Credit Note (simple version)
            CreditNote creditNote = new CreditNote();
            creditNote.setCreditNoteNo("CN" + String.format("%05d", creditNoteRepository.count() + 1));
            creditNote.setSalesOrder(order);
            creditNote.setAmount(order.getTotalAmount());
            creditNote.setReason(reason != null ? reason : "Order cancelled");
            creditNote.setStatus("PENDING");
            creditNote.setCreatedBy(jwtFilter.getCurrentUser());
            creditNoteRepository.save(creditNote);

            // Update Sales Order
            order.setStatus("CANCELLED");
            order.setUpdatedBy(jwtFilter.getCurrentUser());
            order.setUpdatedAt(Instant.now());
            salesOrderRepository.save(order);

            return LogisticUtils.getResponseEntity("Sales Order cancelled and Credit Note " + creditNote.getCreditNoteNo() + " created", HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

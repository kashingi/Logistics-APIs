package com.distributionAndLogisticsManagementPlatform.serviceImpl;

import com.distributionAndLogisticsManagementPlatform.constants.LogisticConstants;
import com.distributionAndLogisticsManagementPlatform.dto.SalesQuotationDto;
import com.distributionAndLogisticsManagementPlatform.dto.SalesQuotationLineDto;
import com.distributionAndLogisticsManagementPlatform.entity.*;
import com.distributionAndLogisticsManagementPlatform.repository.CustomerRepository;
import com.distributionAndLogisticsManagementPlatform.repository.ProductRepository;
import com.distributionAndLogisticsManagementPlatform.repository.SalesOrderRepository;
import com.distributionAndLogisticsManagementPlatform.repository.SalesQuotationRepository;
import com.distributionAndLogisticsManagementPlatform.security.JwtFilter;
import com.distributionAndLogisticsManagementPlatform.service.SalesQuotationService;
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

//Add your annotations here
@Service
public class SalesQuotationServiceImpl implements SalesQuotationService {

    @Autowired
    private SalesQuotationRepository salesQuotationRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private JwtFilter jwtFilter;
    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Override
    public ResponseEntity<String> createSalesQuotation(SalesQuotationDto salesQuotationDto) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
            if (salesQuotationDto.getLines() == null || salesQuotationDto.getLines().isEmpty()) {
                return LogisticUtils.getResponseEntity("At least one line item is required", HttpStatus.BAD_REQUEST);
            }
            if (salesQuotationDto.getSalespersonName() == null || salesQuotationDto.getSalespersonCode() == null) {
                return LogisticUtils.getResponseEntity("Salesperson name and code are required", HttpStatus.BAD_REQUEST);
            }

            // Handle Customer
            Customer customer;
            if (salesQuotationDto.getCustomerId() != null) {
                Optional<Customer> existing = customerRepository.findById(salesQuotationDto.getCustomerId());
                if (existing.isEmpty()) {
                    return LogisticUtils.getResponseEntity("Customer not found", HttpStatus.BAD_REQUEST);
                }
                customer = existing.get();

                // Update customer details if provided
                if (salesQuotationDto.getPhone() != null) customer.setPhone(salesQuotationDto.getPhone());
                if (salesQuotationDto.getFirstName() != null) customer.setFirstName(salesQuotationDto.getFirstName());
                if (salesQuotationDto.getLastName() != null) customer.setLastName(salesQuotationDto.getLastName());
                if (salesQuotationDto.getEmail() != null) customer.setEmail(salesQuotationDto.getEmail());
                if (salesQuotationDto.getKraPin() != null) customer.setKraPin(salesQuotationDto.getKraPin());
                customerRepository.save(customer);
            } else {
                // Create new customer
                if (salesQuotationDto.getPhone() == null || salesQuotationDto.getFirstName() == null || salesQuotationDto.getLastName() == null) {
                    return LogisticUtils.getResponseEntity("Phone, First Name and Last Name are required for new customer", HttpStatus.BAD_REQUEST);
                }

                customer = new Customer();
                customer.setName(salesQuotationDto.getCustomerName() != null ? salesQuotationDto.getCustomerName() : salesQuotationDto.getFirstName() + " " + salesQuotationDto.getLastName());
                customer.setFirstName(salesQuotationDto.getFirstName());
                customer.setLastName(salesQuotationDto.getLastName());
                customer.setPhone(salesQuotationDto.getPhone());
                customer.setEmail(salesQuotationDto.getEmail());
                customer.setKraPin(salesQuotationDto.getKraPin());
                customer.setCreatedBy(jwtFilter.getCurrentUser());
                customer = customerRepository.save(customer);
            }

            SalesQuotation quotation = new SalesQuotation();
            quotation.setSqNumber("SQ" + String.format("%05d", salesQuotationRepository.count() + 1));
            quotation.setDate(salesQuotationDto.getDate() != null ? salesQuotationDto.getDate() : LocalDate.now());
            quotation.setCustomer(customer);
            quotation.setKraPin(salesQuotationDto.getKraPin());
            quotation.setSalespersonName(salesQuotationDto.getSalespersonName());
            quotation.setSalespersonCode(salesQuotationDto.getSalespersonCode());
            quotation.setStatus("CREATED");
            quotation.setNotes(salesQuotationDto.getNotes());
            quotation.setTerms(salesQuotationDto.getTerms());
            quotation.setBuildingLandmark(salesQuotationDto.getBuildingLandmark());
            quotation.setStreet(salesQuotationDto.getStreet());
            quotation.setHouseNo(salesQuotationDto.getHouseNo());
            quotation.setContactPerson(salesQuotationDto.getContactPerson());
            quotation.setContactPhone(salesQuotationDto.getContactPhone());
            quotation.setCreatedBy(jwtFilter.getCurrentUser());
            quotation.setUpdatedBy(jwtFilter.getCurrentUser());

            BigDecimal totalAmount = BigDecimal.ZERO;

            for (SalesQuotationLineDto lineDto : salesQuotationDto.getLines()) {
                Optional<Product> productOpt = productRepository.findById(lineDto.getProductId());
                if (productOpt.isEmpty()) {
                    return LogisticUtils.getResponseEntity("Product not found: " + lineDto.getProductId(), HttpStatus.BAD_REQUEST);
                }

                Product product = productOpt.get();

                SalesQuotationLine line = new SalesQuotationLine();
                line.setProduct(product);
                line.setQuantity(lineDto.getQuantity());
                line.setUom(product.getUom() != null ? product.getUom().name() : null);
                line.setPriceTier(lineDto.getPriceTier() != null ? lineDto.getPriceTier() : "RETAIL");
                line.setDiscount(lineDto.getDiscount() != null ? lineDto.getDiscount() : BigDecimal.ZERO);
                line.setFulfilment(lineDto.getFulfilment() != null ? lineDto.getFulfilment() : "DELIVERY");

                // Get price based on tier
                BigDecimal unitPrice;
                if ("WHOLESALE".equals(line.getPriceTier())) {
                    unitPrice = product.getWholesalePrice() != null ? product.getWholesalePrice() : product.getRetailPrice();
                } else {
                    unitPrice = product.getRetailPrice() != null ? product.getRetailPrice() : BigDecimal.ZERO;
                }
                line.setUnitPrice(unitPrice);

                BigDecimal lineTotal = lineDto.getQuantity().multiply(unitPrice).subtract(line.getDiscount());
                line.setAmount(lineTotal.setScale(2, RoundingMode.HALF_UP));

                totalAmount = totalAmount.add(line.getAmount());
                quotation.addLine(line);
            }

            quotation.setTotalAmount(totalAmount.setScale(2, RoundingMode.HALF_UP));
            salesQuotationRepository.save(quotation);

            return LogisticUtils.getResponseEntity("Sales Quotation created successfully: " + quotation.getSqNumber(), HttpStatus.CREATED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getAllSalesQuotations() {
        try {
            List<SalesQuotation> salesQuotations = salesQuotationRepository.findAll();

            return LogisticUtils.getResponseEntity("Sales Quotations retrieved successfully", salesQuotations, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getSalesQuotationById(Long id) {
        try {
            Optional<SalesQuotation> quotation = salesQuotationRepository.findById(id);
            if (quotation.isEmpty()) {
                return LogisticUtils.getResponseEntity("Sales Quotation not found", HttpStatus.NOT_FOUND);
            }
            return LogisticUtils.getResponseEntity("Sales Quotation retrieved successfully", quotation.get(), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getSalesQuotationsByStatus(String status) {
        try {

            if (!status.trim().equalsIgnoreCase("DRAFT") && !status.trim().equalsIgnoreCase("CREATED") && !status.trim().equalsIgnoreCase("CANCELLED") && !status.trim().equalsIgnoreCase("CONVERTED")) {
                return LogisticUtils.getResponseEntity("Invalid sales quotation status", HttpStatus.BAD_REQUEST);
            }
            List<SalesQuotation> salesQuotations = salesQuotationRepository.findByStatus(status);

            return LogisticUtils.getResponseEntity("Sales Quotations retrieved successfully", salesQuotations, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> cancelSalesQuotation(Long id) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
            Optional<SalesQuotation> optional = salesQuotationRepository.findById(id);
            if (optional.isEmpty()) {
                return LogisticUtils.getResponseEntity("Sales Quotation not found", HttpStatus.NOT_FOUND);
            }

            SalesQuotation quotation = optional.get();
            quotation.setStatus("CANCELLED");
            quotation.setUpdatedBy(jwtFilter.getCurrentUser());
            quotation.setUpdatedAt(Instant.now());
            salesQuotationRepository.save(quotation);

            return LogisticUtils.getResponseEntity("Sales Quotation cancelled successfully", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @Transactional
    public ResponseEntity<String> convertToSalesOrder(Long id) {
        try {
            if (!jwtFilter.isAdmin()) {
                return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
            Optional<SalesQuotation> quotationOpt = salesQuotationRepository.findById(id);
            if (quotationOpt.isEmpty()) {
                return LogisticUtils.getResponseEntity("Sales Quotation not found", HttpStatus.NOT_FOUND);
            }
            SalesQuotation quotation = quotationOpt.get();

            if ("CREATED".equals(quotation.getStatus())) {
                return LogisticUtils.getResponseEntity("Only CREATED quotations can be converted to Sales Order. Current status: " + quotation.getStatus(), HttpStatus.BAD_REQUEST);
            }
            // Create new Sales Order from Quotation
            SalesOrder order = new SalesOrder();
            order.setSoNumber("SO" + String.format("%05d", salesOrderRepository.count() + 1));
            order.setDate(LocalDate.now());
            order.setCustomer(quotation.getCustomer());
            order.setKraPin(quotation.getKraPin());
            order.setSalespersonName(quotation.getSalespersonName());
            order.setSalespersonCode(quotation.getSalespersonCode());
            order.setSourceQuotation(quotation);
            order.setOrigin("INTERNAL");
            order.setStatus("DRAFT");          // or PENDING if you want it confirmed immediately
            order.setNotes(quotation.getNotes());
            order.setTerms(quotation.getTerms());
            order.setBuildingLandmark(quotation.getBuildingLandmark());
            order.setStreet(quotation.getStreet());
            order.setHouseNo(quotation.getHouseNo());
            order.setContactPerson(quotation.getContactPerson());
            order.setContactPhone(quotation.getContactPhone());
            order.setCreatedBy(jwtFilter.getCurrentUser());
            order.setUpdatedBy(jwtFilter.getCurrentUser());

            BigDecimal totalAmount = BigDecimal.ZERO;

            // Copy all lines from Quotation to Sales Order
            for (SalesQuotationLine qLine : quotation.getLines()) {
                SalesOrderLine orderLine = new SalesOrderLine();
                orderLine.setProduct(qLine.getProduct());
                orderLine.setQuantity(qLine.getQuantity());
                orderLine.setUom(qLine.getUom());
                orderLine.setPriceTier(qLine.getPriceTier());
                orderLine.setUnitPrice(qLine.getUnitPrice());
                orderLine.setDiscount(qLine.getDiscount());
                orderLine.setAmount(qLine.getAmount());
                orderLine.setFulfilment(qLine.getFulfilment());

                totalAmount = totalAmount.add(qLine.getAmount());
                order.addLine(orderLine);
            }

            order.setTotalAmount(totalAmount.setScale(2, RoundingMode.HALF_UP));
            salesOrderRepository.save(order);

            // Update Quotation status
            quotation.setStatus("CONVERTED");
            quotation.setUpdatedBy(jwtFilter.getCurrentUser());
            quotation.setUpdatedAt(Instant.now());
            salesQuotationRepository.save(quotation);

            return LogisticUtils.getResponseEntity("Sales Quotation successfully converted to Sales Order: " + order.getSoNumber(), HttpStatus.CREATED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

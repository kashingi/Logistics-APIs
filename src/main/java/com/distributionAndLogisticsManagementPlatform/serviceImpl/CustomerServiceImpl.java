package com.distributionAndLogisticsManagementPlatform.serviceImpl;

import com.distributionAndLogisticsManagementPlatform.constants.LogisticConstants;
import com.distributionAndLogisticsManagementPlatform.dto.CustomerDto;
import com.distributionAndLogisticsManagementPlatform.entity.Customer;
import com.distributionAndLogisticsManagementPlatform.repository.CustomerRepository;
import com.distributionAndLogisticsManagementPlatform.security.JwtFilter;
import com.distributionAndLogisticsManagementPlatform.service.CustomerService;
import com.distributionAndLogisticsManagementPlatform.utils.LogisticUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

//Add your annotations here
@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private JwtFilter jwtFilter;

    private String generateCustomerNo() {
        long next = customerRepository.count() + 1;
        return String.format("CUS%05d", next);
    }

    private String buildFullName(CustomerDto dto) {
        if (dto.getName() != null && !dto.getName().isBlank()) {
            return dto.getName().trim();
        }
        String first = dto.getFirstName() != null ? dto.getFirstName().trim() : "";
        String last = dto.getLastName() != null ? dto.getLastName().trim() : "";
        return (first + " " + last).trim();
    }
    @Override
    public ResponseEntity<?> addCustomer(CustomerDto customerDto) {
        try {
            log.info("Inside addCustomer");

            if (customerDto.getPhone() == null || customerDto.getPhone().isBlank()) {
                return LogisticUtils.getResponseEntity("Phone is required", HttpStatus.BAD_REQUEST);
            }

            String name = buildFullName(customerDto);
            if (name.isBlank()) {
                return LogisticUtils.getResponseEntity(
                        "Customer name or first name is required",
                        HttpStatus.BAD_REQUEST
                );
            }

            // Optional uniqueness checks
            if (customerRepository.existsByPhone(customerDto.getPhone().trim())) {
                return LogisticUtils.getResponseEntity("A customer with this phone already exists", HttpStatus.BAD_REQUEST);
            }
            if (customerDto.getEmail() != null && !customerDto.getEmail().isBlank() && customerRepository.existsByEmail(customerDto.getEmail().trim())) {
                return LogisticUtils.getResponseEntity("A customer with this email already exists", HttpStatus.BAD_REQUEST);
            }

            Customer customer = new Customer();
            customer.setCustomerNo(generateCustomerNo());
            customer.setName(name);
            customer.setFirstName(customerDto.getFirstName());
            customer.setLastName(customerDto.getLastName());
            customer.setPhone(customerDto.getPhone().trim());
            customer.setEmail(customerDto.getEmail());
            customer.setKraPin(customerDto.getKraPin());
            customer.setStreet(customerDto.getStreet());
            customer.setCity(customerDto.getCity());
            customer.setPostalCode(customerDto.getPostalCode());
            customer.setBuildingLandmark(customerDto.getBuildingLandmark());
            customer.setHouseNo(customerDto.getHouseNo());
            customer.setContactPerson(customerDto.getContactPerson());
            customer.setContactPhone(customerDto.getContactPhone());
            customer.setStatus("ACTIVE");
            customer.setCreatedBy(jwtFilter.getCurrentUser());
            customer.setUpdatedBy(jwtFilter.getCurrentUser());
            customer.setCreatedAt(Instant.now());
            customer.setUpdatedAt(Instant.now());

            customerRepository.save(customer);

            return LogisticUtils.getResponseEntity("Customer created successfully", HttpStatus.CREATED);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getAllCustomers() {
        try {
            List<Customer> customers = customerRepository.findAll();
            return LogisticUtils.getResponseEntity("Customers retrieved successfully", customers, HttpStatus.OK
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getCustomerById(Long id) {
        try {
            Optional<Customer> customer = customerRepository.findById(id);
            if (customer.isEmpty()) {
                return LogisticUtils.getResponseEntity("Customer not found", HttpStatus.NOT_FOUND);
            }
            return LogisticUtils.getResponseEntity("Customer retrieved successfully", customer.get(), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getCustomersByStatus(String status) {
        try {
            if (!status.trim().equalsIgnoreCase("ACTIVE") && !status.trim().equalsIgnoreCase("INACTIVE")) {
                return LogisticUtils.getResponseEntity("Invalid customer status", HttpStatus.BAD_REQUEST);
            }
            List<Customer> customers = customerRepository.findByStatus(status.toUpperCase(Locale.ROOT));
            return LogisticUtils.getResponseEntity("Customers retrieved successfully", customers, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> searchCustomers(String keyword) {
        try {
            if (keyword == null || keyword.isBlank()) {
                return getAllCustomers();
            }
            String q = keyword.trim().toLowerCase(Locale.ROOT);
            List<Customer> customers = customerRepository.findAll().stream()
                    .filter(c -> (c.getName() != null && c.getName().toLowerCase(Locale.ROOT).contains(q))
                                    || (c.getPhone() != null && c.getPhone().toLowerCase(Locale.ROOT).contains(q))
                                    || (c.getEmail() != null && c.getEmail().toLowerCase(Locale.ROOT).contains(q))
                                    || (c.getCustomerNo() != null && c.getCustomerNo().toLowerCase(Locale.ROOT).contains(q))
                    )
                    .collect(Collectors.toList());

            return LogisticUtils.getResponseEntity("Customers retrieved successfully", customers, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> updateCustomer(Long id, CustomerDto customerDto) {
        try {
            Optional<Customer> optional = customerRepository.findById(id);
            if (optional.isEmpty()) {
                return LogisticUtils.getResponseEntity("Customer not found", HttpStatus.NOT_FOUND);
            }

            Customer customer = optional.get();

            if (customerDto.getPhone() != null && !customerDto.getPhone().isBlank()) {
                Optional<Customer> byPhone = customerRepository.findByPhone(customerDto.getPhone().trim());
                if (byPhone.isPresent() && !byPhone.get().getId().equals(id)) {
                    return LogisticUtils.getResponseEntity("Another customer already uses this phone", HttpStatus.BAD_REQUEST);
                }
                customer.setPhone(customerDto.getPhone().trim());
            }

            String name = buildFullName(customerDto);
            if (!name.isBlank()) {
                customer.setName(name);
            }
            if (customerDto.getFirstName() != null) customer.setFirstName(customerDto.getFirstName());
            if (customerDto.getLastName() != null) customer.setLastName(customerDto.getLastName());
            if (customerDto.getEmail() != null) customer.setEmail(customerDto.getEmail());
            if (customerDto.getKraPin() != null) customer.setKraPin(customerDto.getKraPin());
            if (customerDto.getStreet() != null) customer.setStreet(customerDto.getStreet());
            if (customerDto.getCity() != null) customer.setCity(customerDto.getCity());
            if (customerDto.getPostalCode() != null) customer.setPostalCode(customerDto.getPostalCode());
            if (customerDto.getBuildingLandmark() != null) customer.setBuildingLandmark(customerDto.getBuildingLandmark());
            if (customerDto.getHouseNo() != null) customer.setHouseNo(customerDto.getHouseNo());
            if (customerDto.getContactPerson() != null) customer.setContactPerson(customerDto.getContactPerson());
            if (customerDto.getContactPhone() != null) customer.setContactPhone(customerDto.getContactPhone());
            if (customerDto.getStatus() != null && !customerDto.getStatus().isBlank()) {
                customer.setStatus(customerDto.getStatus());
            }

            customer.setUpdatedBy(jwtFilter.getCurrentUser());
            customer.setUpdatedAt(Instant.now());
            customerRepository.save(customer);

            return LogisticUtils.getResponseEntity(
                    "Customer updated successfully",
                    customer,
                    HttpStatus.OK
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> updateCustomerStatus(Long id, String status) {
        try {
            String normalized = status.toUpperCase(Locale.ROOT).trim();
            if (!normalized.equals("ACTIVE") && !normalized.equals("INACTIVE")) {
                return LogisticUtils.getResponseEntity("Invalid status. Allowed values: ACTIVE, INACTIVE", HttpStatus.BAD_REQUEST);
            }

            Optional<Customer> optional = customerRepository.findById(id);
            if (optional.isEmpty()) {
                return LogisticUtils.getResponseEntity("Customer not found", HttpStatus.NOT_FOUND);
            }

            Customer customer = optional.get();
            customer.setStatus(normalized);
            customer.setUpdatedBy(jwtFilter.getCurrentUser());
            customer.setUpdatedAt(Instant.now());
            customerRepository.save(customer);

            return LogisticUtils.getResponseEntity("Customer status updated successfully", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

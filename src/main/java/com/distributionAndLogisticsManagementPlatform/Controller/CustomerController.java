package com.distributionAndLogisticsManagementPlatform.Controller;

import com.distributionAndLogisticsManagementPlatform.dto.CustomerDto;
import com.distributionAndLogisticsManagementPlatform.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//Add your annotations here
@RestController
@CrossOrigin
@RequestMapping(path = "/v1/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping(path = "/addCustomer")
    public ResponseEntity<?> addCustomer(@RequestBody CustomerDto customerDto) {
        return customerService.addCustomer(customerDto);
    }

    @GetMapping(path = "/getAllCustomers")
    public ResponseEntity<?> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping(path = "/getCustomerById/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @GetMapping(path = "/status/{status}")
    public ResponseEntity<?> getCustomersByStatus(@RequestBody CustomerDto customerDto) {
        return customerService.getCustomersByStatus(customerDto.getStatus());
    }

    @GetMapping(path = "/search")
    public ResponseEntity<?> searchCustomers(@RequestParam String keyword) {
        return customerService.searchCustomers(keyword);
    }

    @PatchMapping(path = "/updateCustomer/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @RequestBody CustomerDto customerDto) {
        return customerService.updateCustomer(id, customerDto);
    }

    @PatchMapping(path = "/updateCustomerStatus/{id}")
    public ResponseEntity<?> updateCustomerStatus(
            @PathVariable Long id,
            @RequestBody CustomerDto customerDto) {
        return customerService.updateCustomerStatus(id, customerDto.getStatus());
    }
}

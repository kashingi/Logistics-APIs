package com.distributionAndLogisticsManagementPlatform.repository;

import com.distributionAndLogisticsManagementPlatform.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

//Add your annotations here
public interface CustomerRepository extends JpaRepository<Customer, Long> {

}

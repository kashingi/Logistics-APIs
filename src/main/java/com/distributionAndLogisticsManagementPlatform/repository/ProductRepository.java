package com.distributionAndLogisticsManagementPlatform.repository;

import com.distributionAndLogisticsManagementPlatform.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//Add your annotations here
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySkuCode(String skuCode);

    Optional<Product> findBySerialNumber(String serialNumber);
}

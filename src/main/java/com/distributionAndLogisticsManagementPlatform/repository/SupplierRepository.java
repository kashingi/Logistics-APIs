package com.distributionAndLogisticsManagementPlatform.repository;

import com.distributionAndLogisticsManagementPlatform.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//Add your annotations here
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    boolean existsByName(String trim);

    List<Supplier> findByStatus(String status);
}

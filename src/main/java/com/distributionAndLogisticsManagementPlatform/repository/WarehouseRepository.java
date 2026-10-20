package com.distributionAndLogisticsManagementPlatform.repository;

import com.distributionAndLogisticsManagementPlatform.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

//    Optional<Warehouse> findByWarehouseNo(String warehouseNo);
//
//    List<Warehouse> findByStatus(WarehouseStatus status);
//
    boolean existsByName(String name);

    boolean existsByStreet(String street);
}

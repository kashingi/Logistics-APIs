package com.distributionAndLogisticsManagementPlatform.repository;

import com.distributionAndLogisticsManagementPlatform.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//Add your annotations here
public interface BatchRepository extends JpaRepository<Batch, Long> {

    Optional<Batch> findByLotNumber(String lotNumber);
}

package com.distributionAndLogisticsManagementPlatform.repository;

import com.distributionAndLogisticsManagementPlatform.entity.SalesQuotation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//Add your annotations here
public interface SalesQuotationRepository extends JpaRepository<SalesQuotation, Long> {

    List<SalesQuotation> findByStatus(String status);
}

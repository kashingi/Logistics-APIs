package com.distributionAndLogisticsManagementPlatform.repository;

import com.distributionAndLogisticsManagementPlatform.entity.CreditNote;
import org.springframework.data.jpa.repository.JpaRepository;

//Add your annotations here
public interface CreditNoteRepository extends JpaRepository<CreditNote, Long> {

}

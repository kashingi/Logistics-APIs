package com.distributionAndLogisticsManagementPlatform.repository;

import com.distributionAndLogisticsManagementPlatform.entity.GoodsReceivedNote;
import org.springframework.data.jpa.repository.JpaRepository;

//Add your annotations here
public interface GoodsReceivedNoteRepository extends JpaRepository<GoodsReceivedNote, Long> {
}

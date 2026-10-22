package com.distributionAndLogisticsManagementPlatform.repository;

import com.distributionAndLogisticsManagementPlatform.entity.GoodsReceivedNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//Add your annotations here
public interface GoodsReceivedNoteRepository extends JpaRepository<GoodsReceivedNote, Long> {
    List<GoodsReceivedNote> findByStatus(String status);
}

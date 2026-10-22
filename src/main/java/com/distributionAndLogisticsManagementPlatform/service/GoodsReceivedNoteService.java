package com.distributionAndLogisticsManagementPlatform.service;

import com.distributionAndLogisticsManagementPlatform.dto.GoodsReceivedNoteDto;
import org.springframework.http.ResponseEntity;

//Add your annotations here
public interface GoodsReceivedNoteService {
    ResponseEntity<String> createGrn(GoodsReceivedNoteDto goodsReceivedNoteDto);

    ResponseEntity<?> getAllGrns();

    ResponseEntity<?> getGrnById(Long id);

    ResponseEntity<String> approveGrn(Long id);
}

package com.distributionAndLogisticsManagementPlatform.service;

import com.distributionAndLogisticsManagementPlatform.dto.ProductDto;
import org.springframework.http.ResponseEntity;

public interface ProductService {
    ResponseEntity<String> createProduct(ProductDto productDto);
}

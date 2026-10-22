package com.distributionAndLogisticsManagementPlatform.service;

import com.distributionAndLogisticsManagementPlatform.dto.ProductDto;
import org.springframework.http.ResponseEntity;

public interface ProductService {
    ResponseEntity<String> createProduct(ProductDto productDto);

    ResponseEntity<?> getAllProducts();

    ResponseEntity<?> getProductById(Long id);

    ResponseEntity<String> updateProduct(Long id, ProductDto productDto);


    ResponseEntity<String> updateStatus(Long id, String status);

    ResponseEntity<?> getProductsByStatus(String status);

    ResponseEntity<?> getProductsByCategory(Long categoryId);
}

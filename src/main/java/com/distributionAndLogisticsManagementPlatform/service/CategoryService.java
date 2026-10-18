package com.distributionAndLogisticsManagementPlatform.service;

import com.distributionAndLogisticsManagementPlatform.dto.CategoryDto;
import org.springframework.http.ResponseEntity;

//Add your annotations here
public interface CategoryService {

    ResponseEntity<String> addCategory(CategoryDto categoryDto);

    ResponseEntity<?> getAllCategories();

    ResponseEntity<?> getCategoryById(Long id);

    ResponseEntity<String> updateCategory(Long id, CategoryDto categoryDto);

    ResponseEntity<String> updateCategoryStatus(Long id, String status);

    ResponseEntity<?> getCategoriesByStatus(String status);
}

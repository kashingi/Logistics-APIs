package com.distributionAndLogisticsManagementPlatform.serviceImpl;

import com.distributionAndLogisticsManagementPlatform.constants.LogisticConstants;
import com.distributionAndLogisticsManagementPlatform.dto.CategoryDto;
import com.distributionAndLogisticsManagementPlatform.entity.Category;
import com.distributionAndLogisticsManagementPlatform.repository.CategoryRepository;
import com.distributionAndLogisticsManagementPlatform.security.JwtFilter;
import com.distributionAndLogisticsManagementPlatform.service.CategoryService;
import com.distributionAndLogisticsManagementPlatform.utils.LogisticUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

//Add your annotations here
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    JwtFilter jwtFilter;
    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public ResponseEntity<String> addCategory(CategoryDto categoryDto) {
        try {
            if (jwtFilter.isAdmin()) {
                if (validatedCategory(categoryDto)) {
                    if (categoryRepository.existsByName(categoryDto.getName())) {
                        return LogisticUtils.getResponseEntity("Category already exists", HttpStatus.BAD_REQUEST);
                    }

                    Category category = new Category();
                    category.setName(categoryDto.getName());
                    category.setStatus(categoryDto.getStatus());
                    category.setCreatedAt(Instant.now());
                    category.setUpdatedAt(Instant.now());

                    categoryRepository.save(category);

                    return LogisticUtils.getResponseEntity("Category added successfully.", HttpStatus.CREATED);
                }
                return LogisticUtils.getResponseEntity("Category name or status should not be null", HttpStatus.BAD_REQUEST);
            }
            return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validatedCategory(CategoryDto categoryDto) {

        return categoryDto.getName() != null && categoryDto.getStatus() != null;
    }

    @Override
    public ResponseEntity<?> getAllCategories() {
        try {
            if (jwtFilter.isAdmin()) {
                List<Category> categories = categoryRepository.findAll();

                return LogisticUtils.getResponseEntity("Categories retrieved successfully.", categories, HttpStatus.OK);
            }
            return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getCategoryById(Long id) {
        try {
            Optional<Category> category = categoryRepository.findById(id);
            if (category.isEmpty()) {
                return LogisticUtils.getResponseEntity("Category not found", HttpStatus.NOT_FOUND);
            }

            return LogisticUtils.getResponseEntity("Category retrieved successfully.", category, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateCategory(Long id, CategoryDto categoryDto) {
        try {
            if (jwtFilter.isAdmin()) {
                Optional<Category> optionalCategory = categoryRepository.findById(id);
                if (optionalCategory.isEmpty()) {
                    return LogisticUtils.getResponseEntity("Category not found", HttpStatus.NOT_FOUND);
                }
                if (categoryRepository.existsByName(categoryDto.getName())) {
                    return LogisticUtils.getResponseEntity("Category name already exists.", HttpStatus.BAD_REQUEST);
                }
                Category updateCategory = optionalCategory.get();
                updateCategory.setName(categoryDto.getName());
                updateCategory.setStatus(categoryDto.getStatus());
                updateCategory.setUpdatedAt(Instant.now());

                categoryRepository.save(updateCategory);

                return LogisticUtils.getResponseEntity("Category updated successfully", HttpStatus.OK);
            }
            return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateCategoryStatus(Long id, String status) {
        try {
            if (jwtFilter.isAdmin()) {
                Optional<Category> updateCategory = categoryRepository.findById(id);
                if (updateCategory.isEmpty()) {
                    return LogisticUtils.getResponseEntity("Category not found.", HttpStatus.NOT_FOUND);
                }

                Category updateStatus = updateCategory.get();
                updateStatus.setStatus(status);

                categoryRepository.save(updateStatus);

                return LogisticUtils.getResponseEntity("Category status updated successfully.", HttpStatus.OK);
            }
            return LogisticUtils.getResponseEntity(LogisticConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getCategoriesByStatus(String status) {
        try {
            if (!status.trim().equalsIgnoreCase("ACTIVE") && !status.trim().equalsIgnoreCase("INACTIVE")) {
                return LogisticUtils.getResponseEntity("Invalid status", HttpStatus.BAD_REQUEST);
            }

            List<Category> categories = categoryRepository.findCategoryByStatus(status);

            return LogisticUtils.getResponseEntity("Categories retrieved successfully.", categories, HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}

package com.distributionAndLogisticsManagementPlatform.Controller;

import com.distributionAndLogisticsManagementPlatform.dto.CategoryDto;
import com.distributionAndLogisticsManagementPlatform.dto.ProductDto;
import com.distributionAndLogisticsManagementPlatform.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//Add your annotations here
@RestController
@CrossOrigin
@RequestMapping(path = "/v1/api/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @PostMapping(path = "/addCategory")
    public ResponseEntity<String> addCategory (@RequestBody CategoryDto categoryDto) {
        return categoryService.addCategory(categoryDto);
    }

    @GetMapping(path = "/getAllCategories")
    public ResponseEntity<?> getAllCategories() {

        return categoryService.getAllCategories();
    }

    @GetMapping("/getCategoryById/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {

        return categoryService.getCategoryById(id);
    }

    @PutMapping("/updateCategory/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable Long id, @RequestBody CategoryDto categoryDto) {
        return categoryService.updateCategory(id, categoryDto);
    }

    @PatchMapping("/updateCategoryStatus/{id}")
    public ResponseEntity<String> updateStatus(@PathVariable Long id, @RequestBody CategoryDto categoryDto) {
        return categoryService.updateCategoryStatus(id, categoryDto.getStatus());
    }

    @GetMapping("/getCategoriesByStatus/{status}")
    public ResponseEntity<?> getCategoriesByStatus(@PathVariable String status) {
        return categoryService.getCategoriesByStatus(status);
    }
}

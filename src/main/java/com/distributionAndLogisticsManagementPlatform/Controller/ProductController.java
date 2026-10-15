package com.distributionAndLogisticsManagementPlatform.Controller;

import com.distributionAndLogisticsManagementPlatform.dto.ProductDto;
import com.distributionAndLogisticsManagementPlatform.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//Add your annotations here
@RestController
@RequestMapping(path = "/v1/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping(path = "/addProduct")
    public ResponseEntity<String> createProduct(@RequestBody ProductDto productDto) {
        return productService.createProduct(productDto);
    }

    @GetMapping(path = "/getAllProducts")
    public ResponseEntity<?> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/getProductById/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<String> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
//        return productService.updateProduct(id, productDto);
//    }

//    @PatchMapping("/{id}/status")
//    public ResponseEntity<String> updateStatus(@PathVariable Long id, @RequestParam String status) {
//        return productService.updateStatus(id, status);
//    }

    // ========== New Endpoints ==========

//    @GetMapping("/search")
//    public ResponseEntity<?> searchProducts(@RequestParam String keyword) {
//        return productService.searchProducts(keyword);
//    }

//    @GetMapping("/status/{status}")
//    public ResponseEntity<?> getProductsByStatus(@PathVariable String status) {
//        return productService.getProductsByStatus(status);
//    }
}

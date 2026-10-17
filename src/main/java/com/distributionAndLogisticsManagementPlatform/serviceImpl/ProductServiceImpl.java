package com.distributionAndLogisticsManagementPlatform.serviceImpl;

import com.distributionAndLogisticsManagementPlatform.constants.LogisticConstants;
import com.distributionAndLogisticsManagementPlatform.dto.ProductDto;
import com.distributionAndLogisticsManagementPlatform.entity.Product;
import com.distributionAndLogisticsManagementPlatform.entity.ProductImage;
import com.distributionAndLogisticsManagementPlatform.enums.ProductStatus;
import com.distributionAndLogisticsManagementPlatform.repository.ProductRepository;
import com.distributionAndLogisticsManagementPlatform.service.ProductService;
import com.distributionAndLogisticsManagementPlatform.utils.LogisticUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//Add your annotations here
@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Override
    public ResponseEntity<String> createProduct(ProductDto productDto) {
        try {
            if (productDto.getSkuCode() == null || productDto.getSkuCode().trim().isEmpty()) {
                return LogisticUtils.getResponseEntity("SKU Code is required", HttpStatus.BAD_REQUEST);
            }
            Optional<Product> existingProduct = productRepository.findBySkuCode(productDto.getSkuCode());
            Optional<Product> optionalProduct = productRepository.findBySerialNumber(productDto.getSerialNumber());
            if (existingProduct.isPresent() || optionalProduct.isPresent()) {
                return LogisticUtils.getResponseEntity("SKU or Serial number already exists", HttpStatus.BAD_REQUEST);
            }
            Product product = new Product();
            product.setName(productDto.getName());
            product.setSerialNumber(productDto.getSerialNumber());
            product.setBrand(productDto.getBrand());
            product.setDescription(productDto.getDescription());
            product.setUom(productDto.getUom());
            product.setPackagingUnit(productDto.getPackagingUnit());
            product.setQuantityPerUnit(productDto.getQuantityPerUnit());
            product.setPurchasePrice(productDto.getPurchasePrice());
            product.setWholesalePrice(productDto.getWholesalePrice());
            product.setRetailPrice(productDto.getRetailPrice());
            product.setSalesTaxRate(productDto.getSalesTaxRate());
            product.setPurchaseTaxRate(productDto.getPurchaseTaxRate());
            product.setSkuCode(productDto.getSkuCode());
            product.setBatchManaged(productDto.isBatchManaged());
            product.setShelfLifeDays(productDto.getShelfLifeDays());
            product.setEarlyWarningDays(productDto.getEarlyWarningDays());
            product.setStatus(ProductStatus.ACTIVE);
            product.setProductNo("IT" + String.format("%04d", productRepository.count() + 1));
            product.setCreatedAt(Instant.now());
            product.setUpdatedAt(Instant.now());

            // Add up to 5 images
            if (productDto.getImageUrls() != null && !productDto.getImageUrls().isEmpty()) {
                List<ProductImage> images = new ArrayList<>();
                int order = 0;
                for (String url : productDto.getImageUrls()) {
                    if (order >= 5) break; // Maximum 5 images

                    ProductImage image = new ProductImage();
                    image.setProduct(product);
                    image.setProductImage(url);
                    image.setSortOrder(order++);
                    images.add(image);
                }
                product.setImages(images);
            }

            productRepository.save(product);

            return LogisticUtils.getResponseEntity("Product created successfully.", HttpStatus.CREATED);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getAllProducts() {
        try {
            List<Product> products = productRepository.findAll();

            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> getProductById(Long id) {
        try {
            Optional<Product> product = productRepository.findById(id);
            if (product.isPresent()) {
                return new ResponseEntity<>(product.get(), HttpStatus.OK);
            } else {
                return LogisticUtils.getResponseEntity("Product not found.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProduct(Long id, ProductDto productDto) {
        try {
            Optional<Product> optionalProduct = productRepository.findById(id);
            if (optionalProduct.isEmpty()) {
                return LogisticUtils.getResponseEntity("Product not found", HttpStatus.NOT_FOUND);
            }

            Product product = optionalProduct.get();
            product.setName(productDto.getName());
            product.setSerialNumber(productDto.getSerialNumber());
            product.setBrand(productDto.getBrand());
            product.setDescription(productDto.getDescription());
            product.setUom(productDto.getUom());
            product.setPackagingUnit(productDto.getPackagingUnit());
            product.setQuantityPerUnit(productDto.getQuantityPerUnit());
            product.setPurchasePrice(productDto.getPurchasePrice());
            product.setWholesalePrice(productDto.getWholesalePrice());
            product.setRetailPrice(productDto.getRetailPrice());
            product.setSalesTaxRate(productDto.getSalesTaxRate());
            product.setPurchaseTaxRate(productDto.getPurchaseTaxRate());
            product.setBatchManaged(productDto.isBatchManaged());
            product.setShelfLifeDays(productDto.getShelfLifeDays());
            product.setEarlyWarningDays(productDto.getEarlyWarningDays());
            product.setUpdatedAt(Instant.now());

            productRepository.save(product);

            return LogisticUtils.getResponseEntity("Product updated successfully.", HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Long id, String status) {
        try {
            Optional<Product> productOptional = productRepository.findById(id);
            if (productOptional.isEmpty()) {
                return LogisticUtils.getResponseEntity("Product not found", HttpStatus.NOT_FOUND);
            }

            Product product = productOptional.get();
            product.setStatus(ProductStatus.valueOf(status));
            product.setUpdatedAt(Instant.now());

            productRepository.save(product);

            return LogisticUtils.getResponseEntity("Product status updated successfully.", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getProductsByStatus(String status) {
        try {
            if (!status.trim().equalsIgnoreCase("ACTIVE") && !status.trim().equalsIgnoreCase("INACTIVE")) {
                return LogisticUtils.getResponseEntity("Invalid status", HttpStatus.BAD_REQUEST);
            }
            ProductStatus productStatus = ProductStatus.valueOf(status.toUpperCase().trim());

            List<Product> products = productRepository.findByStatus(productStatus);

            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}

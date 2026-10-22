package com.distributionAndLogisticsManagementPlatform.serviceImpl;

import com.distributionAndLogisticsManagementPlatform.entity.*;
import com.distributionAndLogisticsManagementPlatform.repository.BatchRepository;
import com.distributionAndLogisticsManagementPlatform.repository.StockLevelRepository;
import com.distributionAndLogisticsManagementPlatform.repository.StockMovementRepository;
import com.distributionAndLogisticsManagementPlatform.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

//Add your annotations here
@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private StockLevelRepository stockLevelRepository;
    @Autowired
    private StockMovementRepository stockMovementRepository;
    @Autowired
    private BatchRepository batchRepository;

    /**
     * Increase stock (used by GRN, Stock In, Transfer Receive)
     */
    @Override
    public void increaseStock(Product product, Warehouse warehouse, BigDecimal quantity, String batchNo, String movementType, String referenceType, Long referenceId, String createdBy) {

        Batch batch = null;

        if (batchNo != null && !batchNo.isBlank()) {
            batch = batchRepository.findByLotNumber(batchNo).orElseGet(() -> {
                Batch newBatch = new Batch();
                newBatch.setLotNumber(batchNo);
                newBatch.setProduct(product);
                newBatch.setWarehouse(warehouse);
                newBatch.setQuantityReceived(quantity);
                newBatch.setQuantityRemaining(quantity);
                newBatch.setReceivedDate(LocalDate.now());

                // Calculate expiry if product has shelf life
                if (product.getShelfLifeDays() != null && product.getShelfLifeDays() > 0) {
                    newBatch.setExpiryDate(LocalDate.now().plusDays(product.getShelfLifeDays()));
                }
                return batchRepository.save(newBatch);
            });

            // Update remaining quantity if batch already existed
            batch.setQuantityRemaining(batch.getQuantityRemaining().add(quantity));
            batchRepository.save(batch);
        }

        // Update or create StockLevel
        StockLevel stockLevel = findOrCreateStockLevel(product, warehouse, batch);
        stockLevel.setQuantityOnHand(stockLevel.getQuantityOnHand().add(quantity));
        stockLevelRepository.save(stockLevel);

        // Write to ledger
        createMovement(product, warehouse, batch, quantity, movementType, referenceType, referenceId, createdBy);
    }

    /**
     * Decrease stock (used by Stock Out, Transfer Approve, Sales, etc.)
     */
    @Override
    public void decreaseStock(Product product, Warehouse warehouse, BigDecimal quantity, String batchNo, String movementType, String referenceType, Long referenceId, String createdBy) {

        Batch batch = null;
        if (batchNo != null && !batchNo.isBlank()) {
            batch = batchRepository.findByLotNumber(batchNo)
                    .orElseThrow(() -> new RuntimeException("Batch not found: " + batchNo));
        }

        StockLevel stockLevel = findOrCreateStockLevel(product, warehouse, batch);

        if (stockLevel.getQuantityOnHand().compareTo(quantity) < 0) {
            // Check if warehouse allows negative stock
            if (!warehouse.isAllowNegativeInventory()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
        }

        stockLevel.setQuantityOnHand(stockLevel.getQuantityOnHand().subtract(quantity));
        stockLevelRepository.save(stockLevel);

        if (batch != null) {
            batch.setQuantityRemaining(batch.getQuantityRemaining().subtract(quantity));
            batchRepository.save(batch);
        }

        // Write negative quantity to ledger
        createMovement(product, warehouse, batch, quantity.negate(), movementType, referenceType, referenceId, createdBy);
    }

    private StockLevel findOrCreateStockLevel(Product product, Warehouse warehouse, Batch batch) {
        Optional<StockLevel> optional;

        if (batch != null) {
            optional = stockLevelRepository.findByProductIdAndWarehouseIdAndBatchId(
                    product.getId(), warehouse.getId(), batch.getId());
        } else {
            optional = stockLevelRepository.findByProductIdAndWarehouseIdAndBatchIsNull(
                    product.getId(), warehouse.getId());
        }

        return optional.orElseGet(() -> {
            StockLevel newLevel = new StockLevel();
            newLevel.setProduct(product);
            newLevel.setWarehouse(warehouse);
            newLevel.setBatch(batch);
            newLevel.setQuantityOnHand(BigDecimal.ZERO);
            return stockLevelRepository.save(newLevel);
        });
    }

    private void createMovement(Product product, Warehouse warehouse, Batch batch,
                                BigDecimal quantity, String movementType,
                                String referenceType, Long referenceId, String createdBy) {

        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setWarehouse(warehouse);
        movement.setBatch(batch);
        movement.setQuantity(quantity);
        movement.setMovementType(movementType);
        movement.setReferenceType(referenceType);
        movement.setReferenceId(referenceId);
        movement.setCreatedBy(createdBy);

        stockMovementRepository.save(movement);
    }
}

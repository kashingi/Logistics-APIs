package com.distributionAndLogisticsManagementPlatform.dto;

import com.distributionAndLogisticsManagementPlatform.enums.PackagingUnit;
import com.distributionAndLogisticsManagementPlatform.enums.TaxRate;
import com.distributionAndLogisticsManagementPlatform.enums.Uom;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

//Add your annotations here
@Data
public class ProductDto {

    private String name;
    private String serialNumber;
    private String brand;
    private String description;
    private Uom uom;
    private String status;
    private PackagingUnit packagingUnit;
    private BigDecimal quantityPerUnit;
    private BigDecimal purchasePrice;
    private BigDecimal wholesalePrice;
    private BigDecimal retailPrice;
    private TaxRate salesTaxRate;
    private TaxRate purchaseTaxRate;
    private String skuCode;
    private boolean batchManaged;
    private Integer shelfLifeDays;
    private Integer earlyWarningDays;
    private Long categoryId;

    // For images
    private List<String> imageUrls;
}

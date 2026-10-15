package com.distributionAndLogisticsManagementPlatform.dto;

import com.distributionAndLogisticsManagementPlatform.enums.PackagingUnit;
import com.distributionAndLogisticsManagementPlatform.enums.TaxRate;
import com.distributionAndLogisticsManagementPlatform.enums.Uom;
import lombok.Data;

import java.util.List;

//Add your annotations here
@Data
public class ProductDto {

    private String name;
    private String serialNumber;
    private String brand;
    private String description;
    private Uom uom;
    private PackagingUnit packagingUnit;
    private Float quantityPerUnit;
    private Float purchasePrice;
    private Float wholesalePrice;
    private Float retailPrice;
    private TaxRate salesTaxRate;
    private TaxRate purchaseTaxRate;
    private String skuCode;
    private boolean batchManaged;
    private Integer shelfLifeDays;
    private Integer earlyWarningDays;

    // For images
    private List<String> imageUrls;
}

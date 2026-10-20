package com.distributionAndLogisticsManagementPlatform.entity;

import com.distributionAndLogisticsManagementPlatform.enums.PackagingUnit;
import com.distributionAndLogisticsManagementPlatform.enums.ProductStatus;
import com.distributionAndLogisticsManagementPlatform.enums.TaxRate;
import com.distributionAndLogisticsManagementPlatform.enums.Uom;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

//Add your annotations here
@Data
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String productNo;               // e.g. IT0001

    @Column(unique = true)
    private String serialNumber;            // barcode

    @Column(nullable = false)
    private String name;

    private String brand;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private Uom uom;

    @Enumerated(EnumType.STRING)
    private PackagingUnit packagingUnit;

    private Float quantityPerUnit;

    private Float purchasePrice;
    private Float wholesalePrice;
    private Float retailPrice;

    @Enumerated(EnumType.STRING)
    private TaxRate salesTaxRate;

    @Enumerated(EnumType.STRING)
    private TaxRate purchaseTaxRate;

    @Column(nullable = false, unique = true)
    private String skuCode;

    private boolean batchManaged = false;

    private Integer shelfLifeDays;
    private Integer earlyWarningDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.ACTIVE;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    private String createdBy;
    private String updatedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}

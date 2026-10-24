package com.distributionAndLogisticsManagementPlatform.entity;

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
@Table(name = "deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "sales_order_id", nullable = false)
    private SalesOrder salesOrder;

    private String deliveryType;               // STANDARD, EXPRESS, COLLECTION

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rider_id")
    private Rider rider;

    private String status;         // PENDING, PICKED, IN_PROGRESS, COMPLETED, CANCELLED, RETURNED, COLLECTED

    private String lotNumber;

    @ElementCollection
    @CollectionTable(name = "delivery_item_barcodes", joinColumns = @JoinColumn(name = "delivery_id"))
    @Column(name = "barcode")
    private List<String> scannedBarcodes = new ArrayList<>();

    private Instant pickedAt;
    private Instant dispatchedAt;
    private Instant completedAt;

    private String createdBy;
    private String updatedBy;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}

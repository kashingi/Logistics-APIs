package com.distributionAndLogisticsManagementPlatform.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//add your annotations here
@Data
@Entity
@Table(name = "picking_lists")
public class PickingList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String pickingListNo;               // e.g. PL00001

    private LocalDate date;

    private String deliveryType;                // STANDARD or EXPRESS

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rider_id")
    private Rider rider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    private DeliveryZone zone;

    @ManyToMany
    @JoinTable(name = "picking_list_orders", joinColumns = @JoinColumn(name = "picking_list_id"), inverseJoinColumns = @JoinColumn(name = "sales_order_id"))
    private List<SalesOrder> orders = new ArrayList<>();

    private String status = "NOT_PICKED";       // NOT_PICKED, PICKED

    private String createdBy;
    private String updatedBy;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}

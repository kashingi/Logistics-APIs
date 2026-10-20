package com.distributionAndLogisticsManagementPlatform.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

//Add your annotations here
@Data
@Entity
@Table(name = "warehouse")
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String warehouseNo;          // e.g. WH0001

    @Column(nullable = false)
    private String name;

    private String contactPerson;
    private String phone;
    private String email;

    private String street;
    private String city;
    private String postal;

    private String category;

    @Column(nullable = false)
    private boolean allowNegativeInventory = false;

    private String remark;

    @Column(nullable = false)
    private String status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}

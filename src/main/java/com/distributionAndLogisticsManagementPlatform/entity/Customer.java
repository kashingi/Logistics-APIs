package com.distributionAndLogisticsManagementPlatform.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

//Add your annotations here
@Data
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String customerNo;              // e.g. CUS00001

    @Column(nullable = false)
    private String name;

    private String firstName;
    private String lastName;

    @Column(nullable = false)
    private String phone;

    private String email;

    private String kraPin;                  // KRA PIN (SRS / Kenya tax)

    private String street;
    private String city;
    private String postalCode;
    private String buildingLandmark;
    private String houseNo;

    private String contactPerson;
    private String contactPhone;

    @Column(nullable = false)
    private String status;       // ACTIVE | INACTIVE

    private String createdBy;
    private String updatedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}

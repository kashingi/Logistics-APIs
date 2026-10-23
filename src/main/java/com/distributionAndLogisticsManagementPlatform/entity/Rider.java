package com.distributionAndLogisticsManagementPlatform.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

//Add your annotations here
@Data
@Entity
@Table(name = "riders")
public class Rider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(nullable = false, unique = true)
    private String phone;

    private String nationalIdOrPassport;

    private LocalDate dateJoined;

    private String passportPhotoUrl;

    private String vehicleRegistrationNo;
    private String vehicleType;                 // e.g. Electric scooter, Regular bike, etc.
    private BigDecimal carryingCapacityKg;

    private Integer contractDurationMonths;

    private String status;            // ACTIVE or INACTIVE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_zone_id")
    private DeliveryZone currentZone;

    private boolean isAvailable = true;

    private String createdBy;
    private String updatedBy;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}

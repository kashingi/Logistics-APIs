package com.distributionAndLogisticsManagementPlatform.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//Add your annotations here
@Data
@Entity
@Table(name = "sales_quotations")
public class SalesQuotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sqNumber;                    // e.g. SQ00001

    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private String kraPin;

    private String salespersonName;
    private String salespersonCode;

    private String status;

    private BigDecimal totalAmount;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(columnDefinition = "TEXT")
    private String terms;

    // Delivery details
    private String buildingLandmark;
    private String street;
    private String houseNo;
    private String contactPerson;
    private String contactPhone;

    @OneToMany(mappedBy = "salesQuotation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalesQuotationLine> lines = new ArrayList<>();

    private String createdBy;
    private String updatedBy;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public void addLine(SalesQuotationLine line) {
        lines.add(line);
        line.setSalesQuotation(this);
    }
}

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
@Table(name = "sales_orders")
public class SalesOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String soNumber;                        // e.g. SO00001

    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private String kraPin;

    private String salespersonName;
    private String salespersonCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_quotation_id")
    private SalesQuotation sourceQuotation;         // if converted from quotation

    private String origin; //WEB_APP or INTERNAL

    private String dispatchType;  //COLLECTION EXPRESS STANDARD            // set later when processing

    private String status; //DRAFT RETURNED CANCELLED COMPLETED IN_PROGRESS PROCESSING PENDING

    private BigDecimal totalAmount = BigDecimal.ZERO;

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

    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalesOrderLine> lines = new ArrayList<>();

    private String createdBy;
    private String updatedBy;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public void addLine(SalesOrderLine line) {
        lines.add(line);
        line.setSalesOrder(this);
    }
}

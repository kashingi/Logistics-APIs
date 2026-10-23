package com.distributionAndLogisticsManagementPlatform.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//Add your annotations
@Data
@Entity
@Table(name = "stock_checks")
public class StockCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String stockCheckNo;                // e.g. SC00001

    private LocalDate date;

    private String taskType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String status;

    @OneToMany(mappedBy = "stockCheck", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockCheckLine> lines = new ArrayList<>();

    private String createdBy;
    private String updatedBy;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public void addLine(StockCheckLine line) {
        lines.add(line);
        line.setStockCheck(this);
    }
}

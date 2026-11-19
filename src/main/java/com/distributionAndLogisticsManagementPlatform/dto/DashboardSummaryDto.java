package com.distributionAndLogisticsManagementPlatform.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//Add your annotations here
@Data
public class DashboardSummaryDto {

    // KPI cards
    private long totalOrders;
    private long deliveriesMade;
    private BigDecimal totalSales;
    private long stockAlerts;

    private String totalOrdersChange;      // e.g. "+12% vs previous period"
    private String deliveriesChange;
    private String totalSalesChange;
    private String stockAlertsChange;

    // Sales trend
    private List<String> salesTrendLabels = new ArrayList<>();
    private List<BigDecimal> salesTrendAmounts = new ArrayList<>();
    private List<Long> salesTrendOrderCounts = new ArrayList<>();

    // Deliveries by status (donut)
    private long deliveriesTotal;
    private Map<String, Long> deliveriesByStatus;      // Delivered, In Transit, Pending
    private Map<String, Double> deliveriesByStatusPct;

    // Top selling products
    private List<TopProductDto> topSellingProducts = new ArrayList<>();

    // Low stock
    private List<LowStockItemDto> lowStockItems = new ArrayList<>();

    // Recent activity
    private List<ActivityItemDto> recentActivities = new ArrayList<>();

    // Extra KPIs (optional for other widgets)
    private long activeProducts;
    private long activeCategories;
    private long warehouses;
    private long activeSuppliers;
    private long activeZones;
    private long activeRiders;
}

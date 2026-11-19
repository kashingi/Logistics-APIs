package com.distributionAndLogisticsManagementPlatform.serviceImpl;

import com.distributionAndLogisticsManagementPlatform.constants.LogisticConstants;
import com.distributionAndLogisticsManagementPlatform.dto.ActivityItemDto;
import com.distributionAndLogisticsManagementPlatform.dto.DashboardSummaryDto;
import com.distributionAndLogisticsManagementPlatform.dto.LowStockItemDto;
import com.distributionAndLogisticsManagementPlatform.dto.TopProductDto;
import com.distributionAndLogisticsManagementPlatform.entity.Delivery;
import com.distributionAndLogisticsManagementPlatform.entity.SalesOrder;
import com.distributionAndLogisticsManagementPlatform.entity.SalesOrderLine;
import com.distributionAndLogisticsManagementPlatform.entity.StockLevel;
import com.distributionAndLogisticsManagementPlatform.repository.*;
import com.distributionAndLogisticsManagementPlatform.service.DashboardService;
import com.distributionAndLogisticsManagementPlatform.utils.LogisticUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

//Add your annotations here
@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private static final BigDecimal LOW_STOCK_THRESHOLD = BigDecimal.TEN;

    @Autowired
    private SalesOrderRepository salesOrderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private DeliveryZoneRepository deliveryZoneRepository;
    @Autowired
    private RiderRepository riderRepository;
    @Autowired
    private StockLevelRepository stockLevelRepository;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    // If you have Delivery entity/repo:
    @Autowired(required = false) private DeliveryRepository deliveryRepository;


    @Override
    public ResponseEntity<?> getSummary(Integer days) {
        try {
            int periodDays = (days == null || days <= 0) ? 30 : days;
            LocalDate from = LocalDate.now().minusDays(periodDays - 1L);
            LocalDate prevFrom = from.minusDays(periodDays);
            LocalDate prevTo = from.minusDays(1);

            DashboardSummaryDto summaryDto = new DashboardSummaryDto();

            List<SalesOrder> allOrders = salesOrderRepository.findAll();
            List<SalesOrder> periodOrders = allOrders.stream().filter(o -> o.getDate() != null && !o.getDate().isBefore(from)).toList();
            List<SalesOrder> prevOrders = allOrders.stream().filter(o -> o.getDate() != null
                            && !o.getDate().isBefore(prevFrom) && !o.getDate().isAfter(prevTo)).toList();

            // ===== KPI: Total Orders =====
            summaryDto.setTotalOrders(periodOrders.size());
            summaryDto.setTotalOrdersChange(percentChange(periodOrders.size(), prevOrders.size()) + " vs previous period");

            // ===== KPI: Total Sales =====
            BigDecimal sales = periodOrders.stream()
                    .map(o -> o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal prevSales = prevOrders.stream()
                    .map(o -> o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            summaryDto.setTotalSales(sales.setScale(2, RoundingMode.HALF_UP));
            summaryDto.setTotalSalesChange(percentChange(sales, prevSales) + " vs previous period");

            // ===== Deliveries (if Delivery repo exists; else derive from SO status) =====
            long delivered = 0;
            long inTransit = 0;
            long pendingDelivery = 0;

            if (deliveryRepository != null) {
                List<Delivery> deliveries = deliveryRepository.findAll();
                List<Delivery> periodDeliveries = deliveries.stream().filter(d -> d.getCreatedAt() != null
                                && d.getCreatedAt().isAfter(from.atStartOfDay().toInstant(java.time.ZoneOffset.UTC))).toList();
                delivered = periodDeliveries.stream().filter(d -> "COMPLETED".equalsIgnoreCase(d.getStatus())
                                || "DELIVERED".equalsIgnoreCase(d.getStatus())
                                || "COLLECTED".equalsIgnoreCase(d.getStatus())).count();
                inTransit = periodDeliveries.stream().filter(d -> List.of("IN_PROGRESS", "PICKED", "IN_TRANSIT")
                                .contains(Optional.ofNullable(d.getStatus()).orElse("").toUpperCase())).count();
                pendingDelivery = periodDeliveries.stream().filter(d -> "PENDING".equalsIgnoreCase(d.getStatus())).count();
                summaryDto.setDeliveriesMade(delivered);
            } else {
                // Fallback from sales order statuses
                delivered = periodOrders.stream().filter(o -> "COMPLETED".equalsIgnoreCase(o.getStatus())).count();
                inTransit = periodOrders.stream().filter(o -> List.of("PROCESSING", "IN_PROGRESS")
                                .contains(Optional.ofNullable(o.getStatus()).orElse("").toUpperCase())).count();
                pendingDelivery = periodOrders.stream().filter(o -> List.of("PENDING", "DRAFT")
                                .contains(Optional.ofNullable(o.getStatus()).orElse("").toUpperCase())).count();
                summaryDto.setDeliveriesMade(delivered);
            }
            long deliveryTotal = delivered + inTransit + pendingDelivery;
            summaryDto.setDeliveriesTotal(deliveryTotal == 0 ? delivered : deliveryTotal);
            summaryDto.setDeliveriesChange("+0% vs previous period"); // enhance later with prev period delivery counts

            Map<String, Long> byStatus = new LinkedHashMap<>();
            byStatus.put("Delivered", delivered);
            byStatus.put("In Transit", inTransit);
            byStatus.put("Pending", pendingDelivery);
            summaryDto.setDeliveriesByStatus(byStatus);

            Map<String, Double> pct = new LinkedHashMap<>();
            long base = Math.max(deliveryTotal, 1);
            pct.put("Delivered", roundPct(delivered, base));
            pct.put("In Transit", roundPct(inTransit, base));
            pct.put("Pending", roundPct(pendingDelivery, base));
            summaryDto.setDeliveriesByStatusPct(pct);

            // ===== Low stock / Stock alerts =====
            List<StockLevel> levels = stockLevelRepository.findAll();
            List<LowStockItemDto> lowStock = new ArrayList<>();
            for (StockLevel level : levels) {
                BigDecimal qty = level.getQuantityOnHand() != null ? level.getQuantityOnHand() : BigDecimal.ZERO;
                if (qty.compareTo(LOW_STOCK_THRESHOLD) < 0) {
                    LowStockItemDto item = new LowStockItemDto();
                    if (level.getProduct() != null) {
                        item.setProductId(level.getProduct().getId());
                        item.setProductName(level.getProduct().getName());
                        item.setSkuCode(level.getProduct().getSkuCode());
                    }
                    if (level.getWarehouse() != null) {
                        item.setWarehouseId(level.getWarehouse().getId());
                        item.setWarehouseName(level.getWarehouse().getName());
                    }
                    if (level.getBatch() != null) {
                        item.setBatchNo(level.getBatch().getLotNumber());
                    }
                    item.setQuantityOnHand(qty);
                    item.setMinimumQty(LOW_STOCK_THRESHOLD);
                    lowStock.add(item);
                }
            }
            lowStock.sort(Comparator.comparing(LowStockItemDto::getQuantityOnHand));
            summaryDto.setLowStockItems(lowStock.stream().limit(10).toList());
            summaryDto.setStockAlerts(lowStock.size());
            summaryDto.setStockAlertsChange(lowStock.isEmpty() ? "Stock healthy" : "+" + Math.min(lowStock.size(), 5) + " low stock");

            // ===== Sales trend (by day in period, sample last 30 points max) =====
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd");
            List<String> labels = new ArrayList<>();
            List<BigDecimal> amounts = new ArrayList<>();
            List<Long> orderCounts = new ArrayList<>();

            for (int i = periodDays - 1; i >= 0; i--) {
                LocalDate day = LocalDate.now().minusDays(i);
                labels.add(day.format(fmt));
                List<SalesOrder> dayOrders = allOrders.stream().filter(o -> day.equals(o.getDate())).toList();
                orderCounts.add((long) dayOrders.size());
                amounts.add(dayOrders.stream()
                        .map(o -> o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
            }
            summaryDto.setSalesTrendLabels(labels);
            summaryDto.setSalesTrendAmounts(amounts);
            summaryDto.setSalesTrendOrderCounts(orderCounts);

            // ===== Top selling products (from order lines if available) =====
            Map<Long, TopProductDto> topMap = new HashMap<>();
            for (SalesOrder order : periodOrders) {
                if (order.getLines() == null) continue;
                for (SalesOrderLine line : order.getLines()) {
                    if (line.getProduct() == null) continue;
                    Long pid = line.getProduct().getId();
                    TopProductDto top = topMap.getOrDefault(pid, new TopProductDto());
                    top.setProductId(pid);
                    top.setProductName(line.getProduct().getName());
                    top.setSkuCode(line.getProduct().getSkuCode());
                    BigDecimal qty = line.getQuantity() != null ? line.getQuantity() : BigDecimal.ZERO;
                    top.setUnitsSold(top.getUnitsSold() == null ? qty : top.getUnitsSold().add(qty));
                    top.setGrowthLabel(""); // optional later
                    topMap.put(pid, top);
                }
            }
            List<TopProductDto> topList = topMap.values().stream().sorted((a, b) -> b.getUnitsSold().compareTo(a.getUnitsSold())).limit(5).toList();
            summaryDto.setTopSellingProducts(topList);

            // ===== Recent activities (lightweight, derived) =====
            List<ActivityItemDto> activities = new ArrayList<>();
            allOrders.stream().sorted(Comparator.comparing(SalesOrder::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()))).limit(5)
                    .forEach(o -> {
                        ActivityItemDto a = new ActivityItemDto();
                        a.setType("ORDER");
                        a.setMessage("New order #" + o.getSoNumber() + " placed");
                        a.setSeverity("INFO");
                        a.setCreatedAt(o.getCreatedAt());
                        a.setTimeAgo(timeAgo(o.getCreatedAt()));
                        activities.add(a);
                    });
            lowStock.stream().limit(2).forEach(ls -> {
                ActivityItemDto a = new ActivityItemDto();
                a.setType("STOCK");
                a.setMessage("Stock alert: " + ls.getProductName() + " (Low Stock)");
                a.setSeverity("WARNING");
                a.setCreatedAt(Instant.now());
                a.setTimeAgo("just now");
                activities.add(a);
            });
            summaryDto.setRecentActivities(activities);

            // Extra counts
            summaryDto.setActiveProducts(productRepository.findAll().stream().filter(p -> p.getStatus() != null && "ACTIVE".equalsIgnoreCase(String.valueOf(p.getStatus()))).count());
            summaryDto.setWarehouses(warehouseRepository.count());
            summaryDto.setActiveSuppliers(supplierRepository.count());
            summaryDto.setActiveZones(deliveryZoneRepository.count());
            summaryDto.setActiveRiders(riderRepository.count());

            return LogisticUtils.getResponseEntity("Dashboard summary loaded successfully", summaryDto, HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LogisticUtils.getResponseEntity(LogisticConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String percentChange(long current, long previous) {
        if (previous == 0) return current > 0 ? "+100%" : "0%";
        double percent = ((double) (current - previous) / previous) * 100.0;
        return String.format("%+.0f%%", percent);
    }

    private String percentChange(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return current != null && current.compareTo(BigDecimal.ZERO) > 0 ? "+100%" : "0%";
        }
        BigDecimal percent = current.subtract(previous).multiply(BigDecimal.valueOf(100)).divide(previous, 0, RoundingMode.HALF_UP);

        return (percent.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "") + percent + "%";
    }

    private double roundPct(long part, long total) {
        return Math.round((part * 1000.0 / total)) / 10.0;
    }

    private String timeAgo(Instant instant) {
        if (instant == null) return "";
        long minutes = Duration.between(instant, Instant.now()).toMinutes();
        if (minutes < 1) return "just now";
        if (minutes < 60) return minutes + "m ago";
        long hours = minutes / 60;
        if (hours < 24) return hours + "h ago";
        return (hours / 24) + "d ago";
    }
}



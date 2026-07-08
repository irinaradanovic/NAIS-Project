package com.example.order_management_service.controller;

import com.example.order_management_service.repository.OrderRepository;
import com.example.order_management_service.service.OrderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders/analytics")
public class OrderAnalyticsController {

    private final OrderService orderService;

    public OrderAnalyticsController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/customer-revenue")
    public ResponseEntity<List<OrderRepository.CustomerRevenueSummary>> getCustomerRevenueSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "1") Long minOrders) {
        return ResponseEntity.ok(orderService.getCustomerRevenueSummary(from, to, minOrders));
    }

    @GetMapping("/article-revenue")
    public ResponseEntity<List<OrderRepository.ArticleRevenueByStatus>> getArticleRevenueByStatus(
            @RequestParam String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "1") Long minQuantity) {
        return ResponseEntity.ok(orderService.getArticleRevenueByStatus(status, from, to, minQuantity));
    }

    @GetMapping("/status-revenue")
    public ResponseEntity<List<OrderRepository.StatusRevenueSummary>> getStatusRevenueSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "1") Long minOrders) {
        return ResponseEntity.ok(orderService.getStatusRevenueSummary(from, to, minOrders));
    }

    @PatchMapping("/{orderId}/articles/{articleId}/quantity/increase")
    public ResponseEntity<List<OrderRepository.ArticleQuantityUpdateResult>> increaseArticleQuantityIfExists(
            @PathVariable UUID orderId,
            @PathVariable UUID articleId,
            @RequestParam(defaultValue = "1") Integer increment) {
        return ResponseEntity.ok(orderService.increaseArticleQuantityIfExists(orderId, articleId, increment));
    }

    @PatchMapping("/customers/{customerId}/loyalty-tier/recalculate")
    public ResponseEntity<List<OrderRepository.CustomerLoyaltyUpdateResult>> recalculateCustomerLoyaltyTier(
            @PathVariable String customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(orderService.recalculateCustomerLoyaltyTier(customerId, from, to));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<OrderRepository.RecommendedArticle>> getRecommendedArticlesForCustomer(
            @RequestParam String customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "1") Long minSharedPurchases) {
        return ResponseEntity.ok(orderService.getRecommendedArticlesForCustomer(customerId, from, to, minSharedPurchases));
    }

    @GetMapping("/invoice-mismatches")
    public ResponseEntity<List<OrderRepository.InvoiceMismatch>> getInvoiceMismatchOrders(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0.0") Double tolerance) {
        return ResponseEntity.ok(orderService.getInvoiceMismatchOrders(from, to, tolerance));
    }
}

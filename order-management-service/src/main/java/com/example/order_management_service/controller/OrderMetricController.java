package com.example.order_management_service.controller;

import com.example.order_management_service.dto.OrderMetricRequestDto;
import com.example.order_management_service.dto.PaymentDeliveryDistanceDto;
import com.example.order_management_service.dto.StatusAvgOrderValueDto;
import com.example.order_management_service.dto.TopCityRevenueDto;
import com.example.order_management_service.model.OrderMetric;
import com.example.order_management_service.service.OrderMetricService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-metrics")
public class OrderMetricController {

    private final OrderMetricService orderMetricService;

    public OrderMetricController(OrderMetricService orderMetricService) {
        this.orderMetricService = orderMetricService;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody OrderMetricRequestDto dto) {
        orderMetricService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<OrderMetric>> getAll() {
        return ResponseEntity.ok(orderMetricService.getAll());
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteByOrderId(@PathVariable String orderId) {
        boolean deleted = orderMetricService.deleteByOrderId(orderId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/queries/top-cities")
    public ResponseEntity<List<TopCityRevenueDto>> getTopCitiesByRevenue(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(orderMetricService.getTopCitiesByRevenue(limit));
    }

    @GetMapping("/queries/avg-by-status")
    public ResponseEntity<List<StatusAvgOrderValueDto>> getAvgOrderValueByStatus() {
        return ResponseEntity.ok(orderMetricService.getAvgOrderValueByStatus());
    }

    @GetMapping("/queries/avg-delivery-km-by-payment")
    public ResponseEntity<List<PaymentDeliveryDistanceDto>> getAvgDeliveryDistanceByPayment() {
        return ResponseEntity.ok(orderMetricService.getAvgDeliveryDistanceByPayment());
    }
}

package com.example.order_management_service.controller;

import com.example.order_management_service.dto.OrderDailyCountResponse;
import com.example.order_management_service.dto.OrderStatusCacheRequest;
import com.example.order_management_service.dto.OrderStatusCacheResponse;
import com.example.order_management_service.service.OrderCacheService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/order-cache")
public class OrderCacheController {

    private final OrderCacheService orderCacheService;

    public OrderCacheController(OrderCacheService orderCacheService) {
        this.orderCacheService = orderCacheService;
    }

    @PostMapping("/status")
    public ResponseEntity<Void> putStatus(@RequestBody OrderStatusCacheRequest request) {
        orderCacheService.putOrderStatus(request.getOrderId(), request.getStatus(), request.getTtlMinutes());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/status/{orderId}")
    public ResponseEntity<OrderStatusCacheResponse> getStatus(@PathVariable String orderId) {
        String status = orderCacheService.getOrderStatus(orderId);
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new OrderStatusCacheResponse(orderId, status));
    }

    @DeleteMapping("/status/{orderId}")
    public ResponseEntity<Void> deleteStatus(@PathVariable String orderId) {
        boolean deleted = orderCacheService.deleteOrderStatus(orderId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/daily-count/{date}")
    public ResponseEntity<OrderDailyCountResponse> incrementDailyCount(@PathVariable String date) {
        LocalDate parsed = LocalDate.parse(date);
        long count = orderCacheService.incrementDailyCount(parsed);
        return ResponseEntity.ok(new OrderDailyCountResponse(date, count));
    }

    @GetMapping("/daily-count/{date}")
    public ResponseEntity<OrderDailyCountResponse> getDailyCount(@PathVariable String date) {
        LocalDate parsed = LocalDate.parse(date);
        Long count = orderCacheService.getDailyCount(parsed);
        if (count == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new OrderDailyCountResponse(date, count));
    }
}

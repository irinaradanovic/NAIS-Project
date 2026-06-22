package com.example.order_management_service.controller;

import com.example.order_management_service.dto.OrderStatusEventRequestDto;
import com.example.order_management_service.model.OrderStatusEvent;
import com.example.order_management_service.service.OrderStatusEventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-status-events")
public class OrderStatusEventController {

    private final OrderStatusEventService orderStatusEventService;

    public OrderStatusEventController(OrderStatusEventService orderStatusEventService) {
        this.orderStatusEventService = orderStatusEventService;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody OrderStatusEventRequestDto dto) {
        orderStatusEventService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<OrderStatusEvent>> getAll() {
        return ResponseEntity.ok(orderStatusEventService.getAll());
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteByOrderId(@PathVariable String orderId) {
        boolean deleted = orderStatusEventService.deleteByOrderId(orderId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

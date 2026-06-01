package com.example.order_management_service.controller;

import com.example.order_management_service.dto.DeliveryStatusRequestDto;
import com.example.order_management_service.dto.DeliveryStatusResponseDto;
import com.example.order_management_service.service.DeliveryStatusCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivery-cache")
@RequiredArgsConstructor
public class DeliveryStatusCacheController {

    private final DeliveryStatusCacheService service;

    @PutMapping("/{orderId}")
    public ResponseEntity<Void> put(@PathVariable String orderId, @RequestBody DeliveryStatusRequestDto request) {
        service.put(orderId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<DeliveryStatusResponseDto> get(@PathVariable String orderId) {
        return service.get(orderId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> delete(@PathVariable String orderId) {
        service.delete(orderId);
        return ResponseEntity.noContent().build();
    }
}

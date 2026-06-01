package com.example.order_management_service.controller;

import com.example.order_management_service.model.CourierMetric;
import com.example.order_management_service.service.CourierMetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courier-metrics")
@RequiredArgsConstructor
public class CourierMetricController {

    private final CourierMetricService service;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody CourierMetric metric) {
        service.save(metric);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<CourierMetric>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/courier/{courierId}")
    public ResponseEntity<List<CourierMetric>> findByCourierId(@PathVariable String courierId) {
        return ResponseEntity.ok(service.findByCourierId(courierId));
    }

    @DeleteMapping("/courier/{courierId}")
    public ResponseEntity<Boolean> deleteByCourierId(@PathVariable String courierId) {
        boolean result = service.deleteByCourierId(courierId);
        return result
                ? ResponseEntity.ok(true)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
    }
}

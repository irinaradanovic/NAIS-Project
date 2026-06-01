package com.example.order_management_service.controller;

import com.example.order_management_service.dto.CityDistanceDto;
import com.example.order_management_service.dto.CourierDelayDto;
import com.example.order_management_service.dto.DeliveryAvgByDayDto;
import com.example.order_management_service.model.DeliveryEvent;
import com.example.order_management_service.service.DeliveryEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delivery-events")
@RequiredArgsConstructor
public class DeliveryEventController {

    private final DeliveryEventService service;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody DeliveryEvent event) {
        service.save(event);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<DeliveryEvent>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<DeliveryEvent>> findByOrderId(@PathVariable String orderId) {
        return ResponseEntity.ok(service.findByOrderId(orderId));
    }

    @GetMapping("/courier/{courierId}")
    public ResponseEntity<List<DeliveryEvent>> findByCourierId(@PathVariable String courierId) {
        return ResponseEntity.ok(service.findByCourierId(courierId));
    }

    @DeleteMapping("/order/{orderId}")
    public ResponseEntity<Boolean> deleteByOrderId(@PathVariable String orderId) {
        boolean result = service.deleteByOrderId(orderId);
        return result
                ? ResponseEntity.ok(true)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
    }

    @GetMapping("/avg-by-day")
    public ResponseEntity<List<DeliveryAvgByDayDto>> avgByDay(@RequestParam(defaultValue = "14") int days) {
        return ResponseEntity.ok(service.findAvgDeliveryByDay(days));
    }

    @GetMapping("/top-delayed-couriers")
    public ResponseEntity<List<CourierDelayDto>> topDelayedCouriers(
            @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(service.findTopDelayedCouriers(days, limit));
    }

    @GetMapping("/avg-distance-by-city")
    public ResponseEntity<List<CityDistanceDto>> avgDistanceByCity(@RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(service.findAvgDistanceByCity(days));
    }
}

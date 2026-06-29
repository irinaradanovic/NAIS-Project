package rs.ac.uns.acs.nais.MockDeliveryService.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.MockDeliveryService.dto.CreateMetricRequest;
import rs.ac.uns.acs.nais.MockDeliveryService.dto.MetricResponse;
import rs.ac.uns.acs.nais.MockDeliveryService.service.DeliveryMetricService;

import java.util.List;

@RestController
@RequestMapping("/api/metrics")
public class DeliveryMetricController {

    private final DeliveryMetricService service;

    public DeliveryMetricController(DeliveryMetricService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<MetricResponse> create(@RequestBody CreateMetricRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<MetricResponse>> getAll(
            @RequestParam(defaultValue = "100") int limit) {
        return ResponseEntity.ok(service.getAll(limit));
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<MetricResponse> getById(@PathVariable String id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE (logical)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
package rs.ac.uns.acs.nais.RestaurantTimeSeriesService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.model.PreparationLog;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.service.PreparationLogService;

import java.util.List;

@RestController
@RequestMapping("/api/preparation-logs")
@RequiredArgsConstructor
public class PreparationLogController {

    private final PreparationLogService service;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody PreparationLog log) {
        service.save(log);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/batch")
    public ResponseEntity<Void> saveAll(@RequestBody List<PreparationLog> logs) {
        service.saveAll(logs);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/restaurant/{restaurantId}")
    public ResponseEntity<Boolean> deleteByRestaurantId(@PathVariable String restaurantId) {
        boolean result = service.deleteByRestaurantId(restaurantId);
        return result
                ? ResponseEntity.ok(true)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
    }
}
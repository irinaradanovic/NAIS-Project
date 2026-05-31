package rs.ac.uns.acs.nais.RestaurantTimeSeriesService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.dto.LocationCategoryComparisonDTO;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.dto.RestaurantAvgPreparationDTO;
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

 /*   @PostMapping("/batch")
    public ResponseEntity<Void> saveAll(@RequestBody List<PreparationLog> logs) {
        service.saveAll(logs);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    } */

    @DeleteMapping("/restaurant/{restaurantId}")
    public ResponseEntity<Boolean> deleteByRestaurantId(@PathVariable String restaurantId) {
        boolean result = service.deleteByRestaurantId(restaurantId);
        return result
                ? ResponseEntity.ok(true)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
    }

    @GetMapping
    public ResponseEntity<List<PreparationLog>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<PreparationLog>> findAllByRestaurantId(@PathVariable String restaurantId) {
        return ResponseEntity.ok(service.findAllByRestaurantId(restaurantId));
    }

    @GetMapping("/menu-item/{menuItemId}")
    public ResponseEntity<List<PreparationLog>> findAllByMenuItemId(@PathVariable String menuItemId) {
        return ResponseEntity.ok(service.findAllByMenuItemId(menuItemId));
    }


    @GetMapping("/top3-fastest-restaurants")
    public ResponseEntity<List<RestaurantAvgPreparationDTO>> top3FastestRestaurants() {
        return ResponseEntity.ok(service.findTop3FastestRestaurants());
    }

    @GetMapping("/compare-locations")
    public ResponseEntity<List<LocationCategoryComparisonDTO>> compareLocations(
            @RequestParam String restaurantId1,
            @RequestParam String restaurantId2) {
        return ResponseEntity.ok(service.compareLocationsByCategory(restaurantId1, restaurantId2));
    }
}
package rs.ac.uns.acs.nais.RestaurantTimeSeriesService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.model.MenuStatusEvent;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.service.MenuStatusEventService;

import java.util.List;

@RestController
@RequestMapping("/api/menu-events")
@RequiredArgsConstructor
public class MenuStatusEventController {

    private final MenuStatusEventService service;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody MenuStatusEvent event) {
        service.save(event);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/batch")
    public ResponseEntity<Void> saveAll(@RequestBody List<MenuStatusEvent> events) {
        service.saveAll(events);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/menu/{menuId}")
    public ResponseEntity<Boolean> deleteByMenuId(@PathVariable String menuId) {
        boolean result = service.deleteByMenuId(menuId);
        return result
                ? ResponseEntity.ok(true)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
    }

    @GetMapping
    public ResponseEntity<List<MenuStatusEvent>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/menu/{menuId}")
    public ResponseEntity<List<MenuStatusEvent>> findAllByMenuId(@PathVariable String menuId) {
        return ResponseEntity.ok(service.findAllByMenuId(menuId));
    }
}
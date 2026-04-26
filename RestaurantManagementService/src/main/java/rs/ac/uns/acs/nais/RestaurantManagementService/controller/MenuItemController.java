package rs.ac.uns.acs.nais.RestaurantManagementService.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.MenuItemDTO;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.MenuItem;
import rs.ac.uns.acs.nais.RestaurantManagementService.service.MenuItemService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/menu-items")
public class MenuItemController {

    @Autowired
    private MenuItemService menuItemService;

    // CRUD za MenuItem cvor

    @PostMapping
    public ResponseEntity<MenuItem> createMenuItem(@RequestBody MenuItemDTO menuItem) {
        return ResponseEntity.ok(menuItemService.create(menuItem));
    }

    @GetMapping
    public ResponseEntity<List<MenuItem>> findAll() {
        return ResponseEntity.ok(menuItemService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> findById(@PathVariable Long id) {
        return ResponseEntity.ok(menuItemService.getById(id));
    }

    @GetMapping("/calories")
    public ResponseEntity<List<MenuItem>> findByMaxCalories(@RequestParam Integer max) {
        return ResponseEntity.ok(menuItemService.findByMaxCalories(max));
    }

    @GetMapping("/price")
    public ResponseEntity<List<MenuItem>> findByPriceRange(
            @RequestParam Double min,
            @RequestParam Double max) {
        return ResponseEntity.ok(menuItemService.findByPriceRange(min, max));
    }

    @GetMapping("/prep-time")
    public ResponseEntity<List<MenuItem>> findByMaxPrepTime(@RequestParam Integer max) {
        return ResponseEntity.ok(menuItemService.findByMaxPrepTime(max));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> update(@PathVariable Long id, @RequestBody MenuItemDTO updated) {
        return ResponseEntity.ok(menuItemService.update(id, updated));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        menuItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

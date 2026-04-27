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
    public ResponseEntity<MenuItem> findById(@PathVariable String id) {
        return ResponseEntity.ok(menuItemService.getById(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> update(@PathVariable String id, @RequestBody MenuItemDTO updated) {
        return ResponseEntity.ok(menuItemService.update(id, updated));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        menuItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

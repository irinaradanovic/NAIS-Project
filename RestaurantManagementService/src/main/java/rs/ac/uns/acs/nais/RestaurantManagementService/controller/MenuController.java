package rs.ac.uns.acs.nais.RestaurantManagementService.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.MenuDTO;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Menu;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.MenuType;
import rs.ac.uns.acs.nais.RestaurantManagementService.service.MenuService;

import java.util.List;

@RestController
@RequestMapping("/menus")
@AllArgsConstructor
public class MenuController {

    @Autowired
    private MenuService menuService;

    // CRUD za Menu cvor

    @PostMapping
    public ResponseEntity<Menu> create(@RequestBody MenuDTO dto) {
        return ResponseEntity.ok(menuService.create(dto));
    }
    @GetMapping
    public ResponseEntity<List<Menu>> getAll() {
        return ResponseEntity.ok(menuService.getAll());
    }

    // sve verzije
    @GetMapping("/versions/{menuId}")
    public ResponseEntity<List<Menu>> findAllVersions(@PathVariable Integer menuId) {
        return ResponseEntity.ok(menuService.findAllVersions(menuId));
    }

    // najnovija verzija
    @GetMapping("/versions/{menuId}/latest")
    public ResponseEntity<Menu> findLatestVersion(@PathVariable Integer menuId) {
        return ResponseEntity.ok(menuService.findLatestVersion(menuId));
    }

    // po tipu
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Menu>> findByType(@PathVariable MenuType type) {
        return ResponseEntity.ok(menuService.findByType(type));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Menu> update(@PathVariable Long id, @RequestBody MenuDTO dto) {
        return ResponseEntity.ok(menuService.update(id, dto));
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        menuService.delete(id);
    }

    // CRUD za HAS_CATEGORY granu
}

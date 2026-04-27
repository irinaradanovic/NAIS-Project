package rs.ac.uns.acs.nais.RestaurantManagementService.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.MenuDTO;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Category;
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

    @PostMapping("/restaurant/{restaurantId}")
    public ResponseEntity<Menu> create(@RequestBody MenuDTO dto, @PathVariable String restaurantId) {
        return ResponseEntity.ok(menuService.create(dto, restaurantId));
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


    @PutMapping("/{id}")
    public ResponseEntity<Menu> update(@PathVariable String id, @RequestBody MenuDTO dto) {
        return ResponseEntity.ok(menuService.update(id, dto));
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        menuService.delete(id);
    }

    // CRUD za HAS_CATEGORY granu

    @PostMapping("/{menuId}/categories/{categoryId}")
    public ResponseEntity<Void> addCategory(
            @PathVariable String menuId,
            @PathVariable String categoryId,
            @RequestParam Integer order) {
        menuService.addCategory(menuId, categoryId, order);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{menuId}/categories")
    public ResponseEntity<List<Category>> getCategoriesByMenuId(@PathVariable String menuId) {
        return ResponseEntity.ok(menuService.getCategoriesByMenuId(menuId));
    }

    @PatchMapping("/{menuId}/categories/{categoryId}/order")
    public ResponseEntity<Void> updateCategoryOrder(
            @PathVariable String menuId,
            @PathVariable String categoryId,
            @RequestParam Integer order) {
        menuService.updateCategoryOrder(menuId, categoryId, order);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{menuId}/categories/{categoryId}")
    public ResponseEntity<Void> removeCategory(
            @PathVariable String menuId,
            @PathVariable String categoryId) {
        menuService.removeCategory(menuId, categoryId);
        return ResponseEntity.noContent().build();
    }


}

package rs.ac.uns.acs.nais.RestaurantManagementService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.RestaurantDTO;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Menu;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Restaurant;
import rs.ac.uns.acs.nais.RestaurantManagementService.service.RestaurantService;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    // CRUD za Restaurant cvor

    @PostMapping
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody RestaurantDTO restaurant) {
        return ResponseEntity.ok(restaurantService.create(restaurant));
    }

    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable String id) {
        return ResponseEntity.ok(restaurantService.getById(id));
    }
    @GetMapping("/name/{name}")
    public ResponseEntity<Restaurant> getRestaurantByName(@PathVariable String name) {
        return ResponseEntity.ok(restaurantService.getByName(name));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(@PathVariable String id, @RequestBody RestaurantDTO updated) {
        return ResponseEntity.ok(restaurantService.update(id, updated));
    }

    @DeleteMapping("/{id}")
    public void deleteRestaurant(@PathVariable String id) {
        restaurantService.delete(id);
    }

    // CRUD za HAS_MENU granu

    @PostMapping("/{restaurantId}/menus/{menuId}")
    public ResponseEntity<Void> addMenuToRestaurant(
            @PathVariable String restaurantId,
            @PathVariable String menuId,
            @RequestParam (defaultValue = "true") Boolean active) {
        restaurantService.addMenu(restaurantId, menuId, active);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{restaurantId}/menus/active")
    public ResponseEntity<List<Menu>> getActiveMenusByRestaurantId(@PathVariable String restaurantId){
        return ResponseEntity.ok(restaurantService.getActiveMenusByRestaurantId(restaurantId));
    }

    @PutMapping("/{restaurantId}/menus/{menuId}")
    public ResponseEntity<Void> updateMenuStatus(
            @PathVariable String restaurantId,
            @PathVariable String menuId,
            @RequestParam Boolean active) {
        restaurantService.updateMenuActive(restaurantId, menuId, active);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{restaurantId}/menus/{menuId}")
    public ResponseEntity<Void> removeMenuFromRestaurant(
            @PathVariable String restaurantId,
            @PathVariable String menuId) {
        restaurantService.removeMenu(restaurantId, menuId);
        return ResponseEntity.noContent().build();
    }


}

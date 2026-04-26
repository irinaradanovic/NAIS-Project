package rs.ac.uns.acs.nais.RestaurantManagementService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.RestaurantDTO;
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
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getById(id));
    }
    @GetMapping("/{name}")
    public ResponseEntity<Restaurant> getRestaurantByName(@PathVariable String name) {
        return ResponseEntity.ok(restaurantService.getByName(name));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(@PathVariable Long id, @RequestBody RestaurantDTO updated) {
        return ResponseEntity.ok(restaurantService.update(id, updated));
    }

    @DeleteMapping("/{id}")
    public void deleteRestaurant(@PathVariable Long id) {
        restaurantService.delete(id);
    }

    // CRUD za HAS_MENU granu


}

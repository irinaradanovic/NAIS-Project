package rs.ac.uns.acs.nais.RestaurantManagementService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.CategoryAnalysisDTO;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.DiscountAnalysisDTO;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.RestaurantDTO;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Menu;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Restaurant;
import rs.ac.uns.acs.nais.RestaurantManagementService.service.RestaurantService;

import java.util.List;
import java.util.UUID;

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
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable UUID id) {
        return ResponseEntity.ok(restaurantService.getById(id));
    }
    @GetMapping("/name/{name}")
    public ResponseEntity<Restaurant> getRestaurantByName(@PathVariable String name) {
        return ResponseEntity.ok(restaurantService.getByName(name));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(@PathVariable UUID id, @RequestBody RestaurantDTO updated) {
        return ResponseEntity.ok(restaurantService.update(id, updated));
    }

    @DeleteMapping("/{id}")
    public void deleteRestaurant(@PathVariable UUID id) {
        restaurantService.delete(id);
    }

    // CRUD za HAS_MENU granu

    @PostMapping("/{restaurantId}/menus/{menuId}")
    public ResponseEntity<Void> addMenuToRestaurant(
            @PathVariable UUID restaurantId,
            @PathVariable UUID menuId,
            @RequestParam (defaultValue = "true") Boolean active) {
        restaurantService.addMenu(restaurantId, menuId, active);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{restaurantId}/menus/active")
    public ResponseEntity<List<Menu>> getActiveMenusByRestaurantId(@PathVariable UUID restaurantId){
        return ResponseEntity.ok(restaurantService.getActiveMenusByRestaurantId(restaurantId));
    }

    @PutMapping("/{restaurantId}/menus/{menuId}")
    public ResponseEntity<Void> updateMenuStatus(
            @PathVariable UUID restaurantId,
            @PathVariable UUID menuId,
            @RequestParam Boolean active) {
        restaurantService.updateMenuActive(restaurantId, menuId, active);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{restaurantId}/menus/{menuId}")
    public ResponseEntity<Void> removeMenuFromRestaurant(
            @PathVariable UUID restaurantId,
            @PathVariable UUID menuId) {
        restaurantService.removeMenu(restaurantId, menuId);
        return ResponseEntity.noContent().build();
    }


    // KOMPLEKSNI UPITI
    /*
    * analizira restorane, prolazi kroz njihove menije i kategorije,
    * racuna prosecnu cenu jela po kategoriji i pronalazi kategorije gde je prosecna cena jela manja od opsteg proseka celog restorana.
    * */

    @GetMapping("/analysis/underpriced-categories")
    public ResponseEntity<List<CategoryAnalysisDTO>> getUnderpricedCategories() {
        return ResponseEntity.ok(restaurantService.getUnderpricedCategories());
    }


    /*
    * najpovoljnije kategorije hrane
    * pretrazuje restorane i menije, trazi kategorije sa vise od 2 proizvoda na popustu
    * i izracunava prosecnu cenu nakon popusta za svaku kategoriju
    * */
    @GetMapping("/analysis/discounts")
    public ResponseEntity<List<DiscountAnalysisDTO>> getDiscounts() {
        return ResponseEntity.ok(restaurantService.getGlobalDiscountAnalysis());
    }



}

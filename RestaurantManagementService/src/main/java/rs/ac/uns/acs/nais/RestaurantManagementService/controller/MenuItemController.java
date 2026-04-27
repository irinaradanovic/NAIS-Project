package rs.ac.uns.acs.nais.RestaurantManagementService.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.CheaperSimilarItemsDTO;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.MenuItemDTO;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.MenuItem;
import rs.ac.uns.acs.nais.RestaurantManagementService.service.MenuItemService;

import java.util.List;
import java.util.UUID;

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
    public ResponseEntity<MenuItem> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(menuItemService.getById(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> update(@PathVariable UUID id, @RequestBody MenuItemDTO updated) {
        return ResponseEntity.ok(menuItemService.update(id, updated));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        menuItemService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // KOMPLEKSNI UPITI

    /*
     * pronalazi restoran u kojem se nalazi specificno jelo pa trazi ostala jela iz istog restorana koja pripadaju istoj kategoriji,
     * ali su jeftinija
     * */
    @GetMapping("/{id}/find-similar-cheaper")
    public ResponseEntity<List<CheaperSimilarItemsDTO>> findSimilarCheaper(@PathVariable UUID id) {
        return ResponseEntity.ok(menuItemService.getCheaperSimilarItems(id));
    }
}

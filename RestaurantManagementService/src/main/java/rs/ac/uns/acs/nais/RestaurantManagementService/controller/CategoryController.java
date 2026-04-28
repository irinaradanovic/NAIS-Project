package rs.ac.uns.acs.nais.RestaurantManagementService.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.CategoryDTO;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Category;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.MenuItem;
import rs.ac.uns.acs.nais.RestaurantManagementService.service.CategoryService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // CRUD za Category cvor

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody CategoryDTO category) {
        return ResponseEntity.ok(categoryService.create(category));
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable UUID id, @RequestBody CategoryDTO updated) {
        return ResponseEntity.ok(categoryService.update(id, updated));
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable UUID id) {
        categoryService.delete(id);
    }

    // CRUD za INCLUDES_ITEM granu

    @PostMapping("/{categoryId}/items/{itemId}")
    public ResponseEntity<Void> addItem(
            @PathVariable UUID categoryId,
            @PathVariable UUID itemId,
            @RequestParam(defaultValue = "0.0") Double discount,
            @RequestParam(required = false) LocalDate discountFrom,
            @RequestParam(required = false) LocalDate discountTo) {
        categoryService.addItem(categoryId, itemId, discount, discountFrom, discountTo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{categoryId}/items")
    public ResponseEntity<List<MenuItem>> getItemsByCategoryId(@PathVariable UUID categoryId) {
        return ResponseEntity.ok(categoryService.getItemsByCategoryId(categoryId));
    }

    @PatchMapping("/{categoryId}/items/{itemId}/discount")
    public ResponseEntity<Void> updateDiscount(
            @PathVariable UUID categoryId,
            @PathVariable UUID itemId,
            @RequestParam Double discount,
            @RequestParam(required = false) LocalDate discountFrom,
            @RequestParam(required = false)  LocalDate discountTo) {
        categoryService.updateDiscount(categoryId, itemId, discount, discountFrom, discountTo);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{categoryId}/items/{itemId}")
    public ResponseEntity<Void> removeItem(
            @PathVariable UUID categoryId,
            @PathVariable UUID itemId) {
        categoryService.removeItem(categoryId, itemId);
        return ResponseEntity.noContent().build();
    }


    // KOMPLEKSNI CRUD

    /*
    * postavljanje popusta za odredjenu kategoriju u trajanju od nedelju dana
    *  */
    @PutMapping("/categories/{categoryId}/special-offer")
    public ResponseEntity<String> applySpecialOffer(
            @PathVariable UUID categoryId,
            @RequestParam Double discount) {
        categoryService.setWeeklyDiscountByCategory(categoryId, discount);
        return ResponseEntity.ok("Akcija uspešno aktivirana.");
    }


}

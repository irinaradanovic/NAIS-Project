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

@RestController
@AllArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // CRUD za Category cvor

    // DOBAVITI SVE KATEGORIJE ZA MENI

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody CategoryDTO category) {
        return ResponseEntity.ok(categoryService.create(category));
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable String id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable String id, @RequestBody CategoryDTO updated) {
        return ResponseEntity.ok(categoryService.update(id, updated));
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable String id) {
        categoryService.delete(id);
    }

    // CRUD za INCLUDES_ITEM granu

    @PostMapping("/{categoryId}/items/{itemId}")
    public ResponseEntity<Void> addItem(
            @PathVariable String categoryId,
            @PathVariable String itemId,
            @RequestParam(defaultValue = "0.0") Double discount,
            @RequestParam(required = false) LocalDate discountFrom,
            @RequestParam(required = false) LocalDate discountTo) {
        categoryService.addItem(categoryId, itemId, discount, discountFrom, discountTo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{categoryId}/items")
    public ResponseEntity<List<MenuItem>> getItemsByCategoryId(@PathVariable String categoryId) {
        return ResponseEntity.ok(categoryService.getItemsByCategoryId(categoryId));
    }

    @PatchMapping("/{categoryId}/items/{itemId}/discount")
    public ResponseEntity<Void> updateDiscount(
            @PathVariable String categoryId,
            @PathVariable String itemId,
            @RequestParam Double discount,
            @RequestParam(required = false) LocalDate discountFrom,
            @RequestParam(required = false)  LocalDate discountTo) {
        categoryService.updateDiscount(categoryId, itemId, discount, discountFrom, discountTo);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{categoryId}/items/{itemId}")
    public ResponseEntity<Void> removeItem(
            @PathVariable String categoryId,
            @PathVariable String itemId) {
        categoryService.removeItem(categoryId, itemId);
        return ResponseEntity.noContent().build();
    }


}

package rs.ac.uns.acs.nais.RestaurantManagementService.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.CategoryDTO;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Category;
import rs.ac.uns.acs.nais.RestaurantManagementService.service.CategoryService;

import java.util.List;

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
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @GetMapping("/{name}")
    public ResponseEntity<Category> getCategoryByName(@PathVariable String name) {
        return ResponseEntity.ok(categoryService.getByName(name));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO updated) {
        return ResponseEntity.ok(categoryService.update(id, updated));
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
    }

    // CRUD za INCLUDES_ITEM granu
}

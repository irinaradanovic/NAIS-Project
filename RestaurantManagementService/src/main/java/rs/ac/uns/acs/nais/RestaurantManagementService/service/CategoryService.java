package rs.ac.uns.acs.nais.RestaurantManagementService.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.CategoryDTO;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Category;
import rs.ac.uns.acs.nais.RestaurantManagementService.repository.CategoryRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // KADA SE KREIRA/OBRISE KATEGORIJA TREBA NAPRAVITI NOVU VERZIJU MENIJA

    public Category create(CategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        return categoryRepository.save(category);
    }
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category with id " + id + " not found"));
    }

    public Category getByName(String name){
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Category with name " + name + " not found"));
    }

    public Category update(Long id, CategoryDTO updated) {
        Category c = getById(id);
        if(updated.getName() != null){
            c.setName(updated.getName());
        }
        return categoryRepository.save(c);
    }
    public void delete(Long id) {
        Category category = getById(id);
        if (category == null) {
            throw new RuntimeException("Category with id " + id + " not found");
        }
        categoryRepository.deleteById(id);
    }
}


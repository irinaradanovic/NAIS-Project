package rs.ac.uns.acs.nais.RestaurantManagementService.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.CategoryDTO;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Category;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Menu;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.MenuItem;
import rs.ac.uns.acs.nais.RestaurantManagementService.repository.CategoryRepository;
import rs.ac.uns.acs.nais.RestaurantManagementService.repository.RestaurantRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MenuService menuService;

    @Autowired
    private RestaurantRepository restaurantRepository;

    // KADA SE KREIRA/OBRISE KATEGORIJA TREBA NAPRAVITI NOVU VERZIJU MENIJA

    public Category create(CategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        return categoryRepository.save(category);
    }
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category getById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category with id " + id + " not found"));
    }

    public Category update(UUID id, CategoryDTO updated) {
        Category c = getById(id);
        if(updated.getName() != null){
            c.setName(updated.getName());
        }
        return categoryRepository.save(c);
    }
    public void delete(UUID id) {
        Category category = getById(id);
        if (category == null) {
            throw new RuntimeException("Category with id " + id + " not found");
        }
        categoryRepository.deleteById(id);
    }

    // INCLUDES_ITEMS  grana

    public void addItem(UUID categoryId, UUID itemId, Double discount, LocalDate from, LocalDate to) {
        categoryRepository.addItemToCategory(categoryId, itemId, discount, from, to);
        // napravi novu verziju svih aktivnih menija koji sadrze ovu kategoriju
        /*List<Menu> affectedMenus = categoryRepository.findMenusByCategoryId(categoryId);
        for (Menu menu : affectedMenus) {
            if (menu.getDeactivationDate() == null) {  // samo aktivni meniji
                menuService.createNewVersion(menu, restaurantRepository.findRestaurantByMenuId(menu.getId()).getId());
            }
        } */
    }

    public List<MenuItem> getItemsByCategoryId(UUID categoryId) {
        return categoryRepository.findItemsByCategoryId(categoryId);
    }

    public void updateDiscount(UUID categoryId, UUID itemId, Double discount, LocalDate from, LocalDate to) {
        categoryRepository.updateItemDiscount(categoryId, itemId, discount, from, to);
    }

    public void removeItem(UUID categoryId, UUID itemId) {
        // napravi novu verziju svih aktivnih menija koji sadrze ovu kategoriju
        //List<Menu> affectedMenus = categoryRepository.findMenusByCategoryId(categoryId);
        categoryRepository.removeItemFromCategory(categoryId, itemId);

       /* for (Menu menu : affectedMenus) {
            if (menu.getDeactivationDate() == null) {
                menuService.createNewVersion(menu, restaurantRepository.findRestaurantByMenuId(menu.getId()).getId());
            }
        } */
    }


    public void setWeeklyDiscountByCategory(UUID categoryId, Double discount){
        categoryRepository.setWeeklyDiscountByCategory(categoryId, discount);
        // dobaviti onda i iteme te kategorije kada se popravi bug
    }

}


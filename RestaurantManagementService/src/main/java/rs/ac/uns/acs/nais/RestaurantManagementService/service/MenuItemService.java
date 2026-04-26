package rs.ac.uns.acs.nais.RestaurantManagementService.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.MenuItemDTO;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.MenuItem;
import rs.ac.uns.acs.nais.RestaurantManagementService.repository.MenuItemRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MenuItemService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    // KADA SE KREIRA/OBRISE MENI ITEM TREBA NAPRAVITI NOVU VERZIJU MENIJA
    // KADA SE PROMENI CENA TREBA NAPRAVITI NOVU VERZIJU MENIJA

    public MenuItem create(MenuItemDTO dto) {
        MenuItem menuItem = new MenuItem();
        menuItem.setName(dto.getName());
        menuItem.setPrice(dto.getPrice());
        menuItem.setCalories(dto.getCalories());
        menuItem.setDescription(dto.getDescription());
        menuItem.setQuantity(dto.getQuantity());
        menuItem.setUnit(dto.getUnit());
        menuItem.setTimeMin(dto.getTimeMin());
        menuItem.setTimeMax(dto.getTimeMax());
        return menuItemRepository.save(menuItem);
    }

    public List<MenuItem> getAll() {
        return menuItemRepository.findAll();
    }

    public MenuItem getById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item with id " + id + " not found"));
    }

    public List<MenuItem> findByMaxCalories(Integer maxCalories) {
        return menuItemRepository.findByCaloriesLessThanEqual(maxCalories);
    }

    public List<MenuItem> findByPriceRange(Double min, Double max) {
        return menuItemRepository.findByPriceBetween(min, max);
    }


    public List<MenuItem> findByMaxPrepTime(Integer maxTime) {
        return menuItemRepository.findByMaxPrepTime(maxTime);
    }

    public MenuItem update(Long id, MenuItemDTO updated) {
        MenuItem menuItem = getById(id);
        if(updated.getName() != null){
            menuItem.setName(updated.getName());
        }
        if(updated.getPrice() != null){
            menuItem.setPrice(updated.getPrice());
        }
        if(updated.getCalories() != null){
            menuItem.setCalories(updated.getCalories());
        }
        if(updated.getDescription() != null){
            menuItem.setDescription(updated.getDescription());
        }
        if(updated.getQuantity() != null){
            menuItem.setQuantity(updated.getQuantity());
        }
        if(updated.getUnit() != null){
            menuItem.setUnit(updated.getUnit());
        }
        if(updated.getTimeMin() != null){
            menuItem.setTimeMin(updated.getTimeMin());
        }
        if(updated.getTimeMax() != null){
            menuItem.setTimeMax(updated.getTimeMax());
        }
        return menuItemRepository.save(menuItem);
    }

    public void delete(Long id) {
        Optional<MenuItem> mI = menuItemRepository.findById(id);
        if(mI.isEmpty()){
            throw new RuntimeException("Menu item with id " + id + " not found");
        }
        menuItemRepository.deleteById(id);
    }
}

package rs.ac.uns.acs.nais.RestaurantManagementService.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.MenuDTO;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Category;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Menu;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.MenuCategoryRel;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.MenuType;
import rs.ac.uns.acs.nais.RestaurantManagementService.repository.MenuRepository;
import rs.ac.uns.acs.nais.RestaurantManagementService.repository.RestaurantRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    public Menu create(MenuDTO dto, String restaurantId) {  // za pravljenje inicijalnog menija, sa samo jednom verzijom
        Menu menu = new Menu();
       if (dto.getMenuId() == null){
            Integer nextMenuId = menuRepository.findMaxMenuId() + 1;
            menu.setMenuId(nextMenuId);
        }else{
            menu.setMenuId(dto.getMenuId());
        }
        mapDtoToEntity(dto, menu);
        menu.setVersion(1);
        menu.setActivationDate(LocalDate.now());

        Menu saved = menuRepository.save(menu);

        restaurantRepository.addMenuToRestaurant(restaurantId, saved.getId(), Boolean.TRUE); // dodaj meni odmah u odgovarajuci restoran
        return saved;
    }

    public List<Menu> getAll() {
        return menuRepository.findAll();
    }

    public Menu getById(String id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu with id " + id + " not found"));
    }

    // sve verzije menija
    public List<Menu> findAllVersions(Integer menuId) {
        return menuRepository.findByMenuId(menuId);
    }

    // najnovija verzija
    public Menu findLatestVersion(Integer menuId) {
        return menuRepository.findLatestVersionByMenuId(menuId)
                .orElseThrow(() -> new RuntimeException("Meni with id " + menuId + " not found"));
    }

    public Menu update(String id, MenuDTO dto) {
        Menu existing = getById(id);
        String restaurantId = restaurantRepository.findRestaurantByMenuId(id).getId();
        // ako su promene velike, pravi novu verziju
        if (isMajorChange(existing, dto)) {
            mapDtoToEntity(dto, existing);
            return createNewVersion(existing, restaurantId);
        } else {
            // obican update menija, npr samo opis, ne pravi novu verziju
            if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
            return menuRepository.save(existing);
        }
        // POVEZATI NOVU VERZIJU S RESTORANOM
    }

    // IMPLEMENTIRATI ROLLBACK NA NEKU VERZIJU

    public void delete(String id) {
        Optional<Menu> m = menuRepository.findById(id);
        if(m.isEmpty()){
            throw new RuntimeException("Menu with id " + id + " not found");
        }
        menuRepository.deleteById(id);
    }




    // HAS_CATEGORY grana

    public void addCategory(String menuId, String categoryId, Integer order) {
        Menu existing = getById(menuId);
        // kada napravimo novu kategoriju u meniju, pravi novu verziju menija koja ce imati tu kategoriju
        Menu newVersion = createNewVersion(existing, restaurantRepository.findRestaurantByMenuId(menuId).getId());
        menuRepository.addCategoryToMenu(newVersion.getId(), categoryId, order);
    }

    public List<Category> getCategoriesByMenuId(String menuId) {
        return menuRepository.findCategoriesByMenuId(menuId);
    }

    public void updateCategoryOrder(String menuId, String categoryId, Integer order) {
        menuRepository.updateCategoryOrder(menuId, categoryId, order);
    }

    public void removeCategory(String menuId, String categoryId) {
        Menu existing = getById(menuId);
        // kada izbacimo neku kategoriju iz menija, pravi novu verziju menija koja nece imati tu kategoriju
        Menu newVersion = createNewVersion(existing, restaurantRepository.findRestaurantByMenuId(menuId).getId());
        menuRepository.removeCategoryFromMenu(newVersion.getId(), categoryId);
    }


    public Menu createNewVersion(Menu existing, String restaurantId) {
        // ucitaj postojeci meni sa relacijama sa kategorijama
        Menu existingWithRels = getById(existing.getId());

        // deaktiviraj staru verziju
        existingWithRels.setDeactivationDate(LocalDate.now());
        menuRepository.save(existingWithRels);

        // kreiraj novu verziju sa istim podacima
        Menu newVersion = new Menu();
        newVersion.setMenuId(existingWithRels.getMenuId());   // isti menuId
        newVersion.setName(existingWithRels.getName());
        newVersion.setDescription(existingWithRels.getDescription());
        newVersion.setType(existingWithRels.getType());
        newVersion.setStartTime(existingWithRels.getStartTime());
        newVersion.setEndTime(existingWithRels.getEndTime());
        newVersion.setStartSeasonDate(existingWithRels.getStartSeasonDate());
        newVersion.setEndSeasonDate(existingWithRels.getEndSeasonDate());
        newVersion.setVersion(existingWithRels.getVersion() + 1);  // povecaj verziju
        newVersion.setActivationDate(LocalDate.now());  // ova je sad aktivna, nema deactivation date
        Menu saved = menuRepository.save(newVersion);

        //  kopiraj HAS_CATEGORY grane sa orderom iz postojecih relacija
        if (existingWithRels.getCategories() != null) {
            for (MenuCategoryRel rel : existingWithRels.getCategories()) {
                menuRepository.addCategoryToMenu(
                        saved.getId(),
                        rel.getCategory().getId(),
                        rel.getOrder()
                );
            }
        }

        // postavi active=false na staroj HAS_MENU grani, dodaj novu aktivnu
        restaurantRepository.setMenuInactive(restaurantId, existingWithRels.getId());
        restaurantRepository.addMenuToRestaurant(restaurantId, saved.getId(), Boolean.TRUE);

        return saved;
    }

    // POMOCNE FUNKCIJE

    private boolean isMajorChange(Menu existing, MenuDTO dto) {
        if (dto.getName() != null && !dto.getName().equals(existing.getName())) return true;
        if (dto.getType() != null && !(dto.getType()).equals(existing.getType())) return true;
        if (dto.getStartTime() != null && !dto.getStartTime().equals(existing.getStartTime())) return true;
        if (dto.getEndTime() != null && !dto.getEndTime().equals(existing.getEndTime())) return true;
        if (dto.getStartSeasonDate() != null && !dto.getStartSeasonDate().equals(existing.getStartSeasonDate())) return true;
        if (dto.getEndSeasonDate() != null && !dto.getEndSeasonDate().equals(existing.getEndSeasonDate())) return true;
        return false;
    }

    private void mapDtoToEntity(MenuDTO dto, Menu menu) {
        //Menu menu = new Menu();
        if (dto.getMenuId() != null) menu.setMenuId(dto.getMenuId());
        if (dto.getName() != null) menu.setName(dto.getName());
        if (dto.getDescription() != null) menu.setDescription(dto.getDescription());
        if (dto.getType() != null) menu.setType(dto.getType());
        if (dto.getStartTime() != null) menu.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) menu.setEndTime(dto.getEndTime());
        if (dto.getStartSeasonDate() != null) menu.setStartSeasonDate(dto.getStartSeasonDate());
        if (dto.getEndSeasonDate() != null) menu.setEndSeasonDate(dto.getEndSeasonDate());
        //return menu;
    }

}

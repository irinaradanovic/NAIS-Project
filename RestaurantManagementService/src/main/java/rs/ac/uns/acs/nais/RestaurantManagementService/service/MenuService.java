package rs.ac.uns.acs.nais.RestaurantManagementService.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.MenuDTO;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Menu;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.MenuType;
import rs.ac.uns.acs.nais.RestaurantManagementService.repository.MenuRepository;
import rs.ac.uns.acs.nais.RestaurantManagementService.repository.RestaurantRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    public Menu create(MenuDTO dto) {  // za pravljenje inicijalnog menija, sa samo jednom verzijom
        Menu menu  = mapDtoToEntity(dto);
        menu.setVersion(1);
        menu.setActivationDate(LocalDate.now());

        // dodati menu u restaurant
        return menuRepository.save(menu);
    }

    public List<Menu> getAll() {
        return menuRepository.findAll();
    }

    public Menu getById(Long id) {
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

    public List<Menu> findByType(MenuType type) {
        return menuRepository.findByType(type);
    }

    public Menu update(Long id, MenuDTO dto) {
        Menu existing = getById(id);
        // ako su promene velike, pravi novu verziju
        if (isMajorChange(existing, dto)) {
            existing.setDeactivationDate(LocalDate.now());  // deaktiviraj trenutnu verziju
            menuRepository.save(existing);

            // kreiraj novu verziju i aktiviraj je
            // TREBA IZMENITI ACTIVE STATUS NA GRANI IZMEDJU RESTORANA I MENIJA
            Menu newVersion = mapDtoToEntity(dto);
            newVersion.setMenuId(existing.getMenuId()); // zadržava isti  menuId
            newVersion.setVersion(existing.getVersion() + 1); // povecava verziju
            newVersion.setActivationDate(LocalDate.now());
            return menuRepository.save(newVersion);

            // TREBA KOPIRATI KATEGORIJE NA NOVU VERZIJU
        } else {
            // obican update menija, npr samo opis, ne pravi novu verziju
            if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
            return menuRepository.save(existing);
        }
        // POVEZATI NOVU VERZIJU S RESTORANOM
    }

    // IMPLEMENTIRATI ROLLBACK NA NEKU VERZIJU

    public void delete(Long id) {
        Optional<Menu> m = menuRepository.findById(id);
        if(m.isEmpty()){
            throw new RuntimeException("Menu with id " + id + " not found");
        }
        menuRepository.deleteById(id);
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

    private Menu mapDtoToEntity(MenuDTO dto) {
        Menu menu = new Menu();
        if (dto.getMenuId() != null) menu.setMenuId(dto.getMenuId());
        if (dto.getName() != null) menu.setName(dto.getName());
        if (dto.getDescription() != null) menu.setDescription(dto.getDescription());
        if (dto.getType() != null) menu.setType(dto.getType());
        if (dto.getStartTime() != null) menu.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) menu.setEndTime(dto.getEndTime());
        if (dto.getStartSeasonDate() != null) menu.setStartSeasonDate(dto.getStartSeasonDate());
        if (dto.getEndSeasonDate() != null) menu.setEndSeasonDate(dto.getEndSeasonDate());
        return menu;
    }

}

package rs.ac.uns.acs.nais.RestaurantManagementService.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.CheaperSimilarItemsDTO;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.MenuItemDTO;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Menu;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.MenuItem;
import rs.ac.uns.acs.nais.RestaurantManagementService.repository.CategoryRepository;
import rs.ac.uns.acs.nais.RestaurantManagementService.repository.MenuItemRepository;
import rs.ac.uns.acs.nais.RestaurantManagementService.repository.RestaurantRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MenuItemService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private MenuService menuService;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private final Neo4jClient neo4jClient;


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

    public MenuItem getById(UUID id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item with id " + id + " not found"));
    }

    public MenuItem update(UUID id, MenuItemDTO updated) {
        MenuItem menuItem = getById(id);
        boolean priceChanged = updated.getPrice() != null
                && !updated.getPrice().equals(menuItem.getPrice());

        if (updated.getName() != null) menuItem.setName(updated.getName());
        if (updated.getPrice() != null) menuItem.setPrice(updated.getPrice());
        if (updated.getCalories() != null) menuItem.setCalories(updated.getCalories());
        if (updated.getDescription() != null) menuItem.setDescription(updated.getDescription());
        if (updated.getQuantity() != null) menuItem.setQuantity(updated.getQuantity());
        if (updated.getUnit() != null) menuItem.setUnit(updated.getUnit());
        if (updated.getTimeMin() != null) menuItem.setTimeMin(updated.getTimeMin());
        if (updated.getTimeMax() != null) menuItem.setTimeMax(updated.getTimeMax());
        MenuItem saved = menuItemRepository.save(menuItem);

        // ako se promenila cena, napravi novu verziju svih aktivnih menija koji sadrze ovaj item
        if (priceChanged) {
            List<Menu> affectedMenus = categoryRepository.findMenusByItemId(id);
            for (Menu menu : affectedMenus) {
                if (menu.getDeactivationDate() == null) {
                    menuService.createNewVersion(menu, restaurantRepository.findRestaurantByMenuId(menu.getId()).getId());
                }
            }
        }
        return saved;
    }

    public void delete(UUID id) {
        Optional<MenuItem> mI = menuItemRepository.findById(id);
        if(mI.isEmpty()){
            throw new RuntimeException("Menu item with id " + id + " not found");
        }
        menuItemRepository.deleteById(id);
    }

   /* public List<CheaperSimilarItemsDTO> getCheaperSimilarItems(UUID itemId) {
        return  menuItemRepository.getCheaperSimilarItems(itemId);
    } */



    public List<CheaperSimilarItemsDTO> getCheaperSimilarItems(UUID itemId) {
            String query = """
            MATCH (i:MenuItem {id: $itemId})<-[:INCLUDES_ITEM]-(c:Category)<-[:HAS_CATEGORY]-(m:Menu)<-[:HAS_MENU]-(r:Restaurant)
            WITH r, c, i.price AS currentPrice
            MATCH (r)-[:HAS_MENU]->(:Menu)-[:HAS_CATEGORY]->(c)-[:INCLUDES_ITEM]->(other:MenuItem)
            WHERE other.price < currentPrice
            RETURN other.name AS itemName, other.price AS itemPrice, r.name AS restaurantName
            """;
            return neo4jClient.query(query)
                    .bind(itemId.toString()).to("itemId")
                    .fetchAs(CheaperSimilarItemsDTO.class)
                    .mappedBy((typeSystem, record) -> new CheaperSimilarItemsDTO(
                            record.get("itemName").asString(),
                            record.get("itemPrice").asDouble(),
                            record.get("restaurantName").asString()
                    ))
                    .all()
                    .stream().toList();
        }

}

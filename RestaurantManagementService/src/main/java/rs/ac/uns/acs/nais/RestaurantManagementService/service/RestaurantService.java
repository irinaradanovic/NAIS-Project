package rs.ac.uns.acs.nais.RestaurantManagementService.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.CategoryAnalysisDTO;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.CheaperSimilarItemsDTO;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.DiscountAnalysisDTO;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.RestaurantDTO;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Menu;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Restaurant;
import rs.ac.uns.acs.nais.RestaurantManagementService.repository.RestaurantRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    private final Neo4jClient neo4jClient;

    public Restaurant create(RestaurantDTO dto) {
        Restaurant r = new Restaurant();
        r.setName(dto.getName());
        r.setAddress(dto.getAddress());
        r.setContact(dto.getContact());
        return restaurantRepository.save(r);
    }

    public List<Restaurant> getAll() {
        return restaurantRepository.findAll();
    }

    public Restaurant getById(UUID id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant with id " + id + " not found"));
    }


    public Restaurant getByName(String name){
        return restaurantRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Restaurant with name " + name + " not found"));
    }

    public Restaurant update(UUID id, RestaurantDTO updated) {
        Restaurant existing = getById(id);

        // ako se nista ne promeni, ostavi staro
        if (updated.getName() != null) {
            existing.setName(updated.getName());
        }
        if (updated.getAddress() != null) {
            existing.setAddress(updated.getAddress());
        }
        if (updated.getContact() != null) {
            existing.setContact(updated.getContact());
        }
        return restaurantRepository.save(existing);
    }

    public void delete(UUID id) {
        Optional<Restaurant> rest = restaurantRepository.findById(id);
        if (rest.isEmpty()) {
            throw new RuntimeException("Restaurant with id " + id + " not found");
        }
        restaurantRepository.deleteById(id);
    }


    // HAS_MENU grana

    public void addMenu(UUID restaurantId, UUID menuId, Boolean active) {
        restaurantRepository.addMenuToRestaurant(restaurantId, menuId, active);
    }

    public List<Menu> getActiveMenusByRestaurantId(UUID restaurantId){
        return restaurantRepository.findActiveMenusByRestaurantId(restaurantId);
    }

    public void updateMenuActive(UUID restaurantId, UUID menuId, Boolean active) {
        restaurantRepository.updateMenuActiveStatus(restaurantId, menuId, active);
    }

    public void removeMenu(UUID restaurantId, UUID menuId) {
        restaurantRepository.removeMenuFromRestaurant(restaurantId, menuId);
    }


    // KOMPLEKSNI UPITI
   /* public List<CategoryAnalysisDTO> getUnderpricedCategories(){
        return restaurantRepository.getUnderpricedCategories();
    }

    public List<DiscountAnalysisDTO> getGlobalDiscountAnalysis() {
        return restaurantRepository.getGlobalDiscountAnalysis();
    } */

    public List<CategoryAnalysisDTO> getUnderpricedCategories() {
        String query = """
        MATCH (r:Restaurant)-[:HAS_MENU]->(m:Menu)-[:HAS_CATEGORY]->(c:Category)-[:INCLUDES_ITEM]->(i:MenuItem)
        WITH r, c, avg(i.price) AS avgCatPrice
        MATCH (r)-[:HAS_MENU]->(:Menu)-[:HAS_CATEGORY]->(:Category)-[:INCLUDES_ITEM]->(all:MenuItem)
        WITH r, c, avgCatPrice, avg(all.price) AS restaurantAvg
        WHERE avgCatPrice < restaurantAvg
        RETURN r.name AS restaurantName, c.name AS categoryName,
               avgCatPrice AS avgCatPrice, restaurantAvg AS restaurantAvg
        """;

        return neo4jClient.query(query)
                .fetchAs(CategoryAnalysisDTO.class)
                .mappedBy((typeSystem, record) -> new CategoryAnalysisDTO(
                        record.get("restaurantName").asString(),
                        record.get("categoryName").asString(),
                        record.get("avgCatPrice").asDouble(),
                        record.get("restaurantAvg").asDouble()
                ))
                .all()
                .stream().toList();
    }


    public List<DiscountAnalysisDTO> getGlobalDiscountAnalysis() {
        String query = """
        MATCH (r:Restaurant)-[:HAS_MENU]->(m:Menu)-[:HAS_CATEGORY]->(c:Category)-[rel:INCLUDES_ITEM]->(i:MenuItem)
        WHERE rel.discount > 0
        WITH r, c, count(i) AS dCount, avg(i.price * (1 - rel.discount)) AS price
        WHERE dCount > 2
        RETURN r.name AS restaurantName, c.name AS categoryName,
               dCount AS discountedCount, price AS avgDiscountedPrice
        """;

        return neo4jClient.query(query)
                .fetchAs(DiscountAnalysisDTO.class)
                .mappedBy((typeSystem, record) -> new DiscountAnalysisDTO(
                        record.get("restaurantName").asString(),
                        record.get("categoryName").asString(),
                        record.get("discountedCount").asLong(),
                        record.get("avgDiscountedPrice").asDouble()
                ))
                .all()
                .stream().toList();
    }

    public void rollbackToVersion(UUID restaurantId, UUID menuId) {
        restaurantRepository.rollbackToMenuVersion(restaurantId, menuId);
    }
}

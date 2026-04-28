package rs.ac.uns.acs.nais.RestaurantManagementService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.CheaperSimilarItemsDTO;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.MenuItem;

import java.util.List;
import java.util.UUID;

@Repository
public interface MenuItemRepository extends Neo4jRepository<MenuItem, UUID> {

    @Query("MATCH (mI:MenuItem) WHERE mI.id = $id " +
            "DETACH DELETE mI")
    void deleteById(@Param("id") UUID id);

    @Query("MATCH (mI:MenuItem) WHERE mi.timeMax <= $maxPrepTime RETURN mI")
    List<MenuItem> findByMaxPrepTime(Integer maxPrepTime);

    @Query("MATCH (i:MenuItem) WHERE i.id = $id " +
            "SET i.price = $price RETURN i")
    MenuItem updatePrice(@Param("id") UUID id, @Param("price") Double price);


    @Query("MATCH (i:MenuItem {id: $itemId})<-[:INCLUDES_ITEM]-(c:Category)<-[:HAS_CATEGORY]-(m:Menu)<-[:HAS_MENU]-(r:Restaurant) " +
            "WITH r, c, i.price AS currentPrice " +
            "MATCH (r)-[:HAS_MENU]->(:Menu)-[:HAS_CATEGORY]->(c)-[:INCLUDES_ITEM]->(other:MenuItem) " +
            "WHERE other.price < currentPrice " +
            "RETURN other.name AS itemName, other.price AS itemPrice, r.name AS restaurantName")
    List<CheaperSimilarItemsDTO> getCheaperSimilarItems(@Param("itemId") UUID itemId);
}

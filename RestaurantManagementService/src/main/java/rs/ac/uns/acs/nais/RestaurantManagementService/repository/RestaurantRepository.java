package rs.ac.uns.acs.nais.RestaurantManagementService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.CategoryAnalysisDTO;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.CheaperSimilarItemsDTO;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.DiscountAnalysisDTO;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Menu;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Restaurant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RestaurantRepository extends Neo4jRepository<Restaurant, UUID> {

    Optional<Restaurant> findByName(String name);

    @Query("MATCH (r:Restaurant) WHERE r.id = $id " +
            "DETACH DELETE r")
    void deleteById(@Param("id") UUID id);

    @Query("MATCH (r:Restaurant)-[rel:HAS_MENU]->(m:Menu) " +
            "WHERE r.id = $restaurantId AND m.id = $menuId " +
            "DELETE rel")
    void removeMenuFromRestaurant(@Param("restaurantId") UUID restaurantId,
                                  @Param("menuId") UUID menuId);

    @Query("MATCH (r:Restaurant), (m:Menu) " +
            "WHERE r.id = $restaurantId AND m.id = $menuId " +
            "CREATE (r)-[:HAS_MENU {active: $active}]->(m)")
    void addMenuToRestaurant(@Param("restaurantId") UUID restaurantId,
                             @Param("menuId") UUID menuId,
                             @Param("active") Boolean active);

    @Query("MATCH (r:Restaurant)-[rel:HAS_MENU]->(m:Menu) " +
            "WHERE r.id = $restaurantId AND m.id = $menuId " +
            "SET rel.active = $active")
    void updateMenuActiveStatus(@Param("restaurantId") UUID restaurantId,
                                @Param("menuId") UUID menuId,
                                @Param("active") Boolean active);

    @Query("MATCH (r:Restaurant)-[rel:HAS_MENU]->(m:Menu) " +
            "WHERE r.id = $restaurantId AND rel.active = true " +
            "RETURN m")
    List<Menu> findActiveMenusByRestaurantId(@Param("restaurantId") UUID restaurantId);

    @Query("MATCH (r:Restaurant)-[rel:HAS_MENU]->(m:Menu) " +
            "WHERE r.id = $restaurantId AND m.id = $menuId " +
            "SET rel.active = false")
    void setMenuInactive(@Param("restaurantId") UUID restaurantId, @Param("menuId") UUID menuId);

    @Query("MATCH (r:Restaurant)-[rel:HAS_MENU]->(m:Menu) " +
            "WHERE m.id = $menuId " +
            "RETURN r")
    Restaurant findRestaurantByMenuId(UUID menuId);


    @Query("MATCH (r:Restaurant)-[:HAS_MENU]->(m:Menu)-[:HAS_CATEGORY]->(c:Category)-[:INCLUDES_ITEM]->(i:MenuItem) " +
            "WITH r, c, avg(i.price) AS avgCatPrice " +
            "MATCH (r)-[:HAS_MENU]->(:Menu)-[:HAS_CATEGORY]->(:Category)-[:INCLUDES_ITEM]->(all:MenuItem) " +
            "WITH r, c, avgCatPrice, avg(all.price) AS restaurantAvg " +
            "WHERE avgCatPrice < restaurantAvg " +
            "RETURN r.name AS restaurantName, c.name AS categoryName, " +
            "avgCatPrice AS avgCatPrice, restaurantAvg AS restaurantAvg")
    List<CategoryAnalysisDTO> getUnderpricedCategories();


    @Query("MATCH (r:Restaurant)-[:HAS_MENU]->(m:Menu)-[:HAS_CATEGORY]->(c:Category)-[rel:INCLUDES_ITEM]->(i:MenuItem) " +
            "WHERE rel.discount > 0 " +
            "WITH r, c, count(i) AS dCount, avg(i.price * (1 - rel.discount)) AS price " +
            "WHERE dCount > 2 " +
            "RETURN r.name AS restaurantName, c.name AS categoryName, " +
            "dCount AS discountedCount, price AS avgDiscountedPrice")
    List<DiscountAnalysisDTO> getGlobalDiscountAnalysis();


}

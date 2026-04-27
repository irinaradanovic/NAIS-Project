package rs.ac.uns.acs.nais.RestaurantManagementService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Menu;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Restaurant;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends Neo4jRepository<Restaurant, String> {

    Optional<Restaurant> findByName(String name);

    @Query("MATCH (r:Restaurant) WHERE elementId(r) = $id " +
            "DETACH DELETE r")
    void deleteById(@Param("id") String id);

    @Query("MATCH (r:Restaurant)-[rel:HAS_MENU]->(m:Menu) " +
            "WHERE elementId(r) = $restaurantId AND elementId(m) = $menuId " +
            "DELETE rel")
    void removeMenuFromRestaurant(@Param("restaurantId") String restaurantId,
                                  @Param("menuId") String menuId);

    @Query("MATCH (r:Restaurant), (m:Menu) " +
            "WHERE elementId(r) = $restaurantId AND elementId(m) = $menuId " +
            "CREATE (r)-[:HAS_MENU {active: $active}]->(m)")
    void addMenuToRestaurant(@Param("restaurantId") String restaurantId,
                             @Param("menuId") String menuId,
                             @Param("active") Boolean active);

    @Query("MATCH (r:Restaurant)-[rel:HAS_MENU]->(m:Menu) " +
            "WHERE elementId(r) = $restaurantId AND elementId(m) = $menuId " +
            "SET rel.active = $active")
    void updateMenuActiveStatus(@Param("restaurantId") String restaurantId,
                                @Param("menuId") String menuId,
                                @Param("active") Boolean active);

    @Query("MATCH (r:Restaurant)-[rel:HAS_MENU]->(m:Menu) " +
            "WHERE elementId(r) = $restaurantId AND rel.active = true " +
            "RETURN m")
    List<Menu> findActiveMenusByRestaurantId(@Param("restaurantId") String restaurantId);

    @Query("MATCH (r:Restaurant)-[rel:HAS_MENU]->(m:Menu) " +
            "WHERE elementId(r) = $restaurantId AND elementId(m) = $menuId " +
            "SET rel.active = false")
    void setMenuInactive(@Param("restaurantId") String restaurantId, @Param("menuId") String menuId);

    @Query("MATCH (r:Restaurant)-[rel:HAS_MENU]->(m:Menu) " +
            "WHERE elementId(m) = menuId" +
            "RETURN r")
    Restaurant findRestaurantByMenuId(String menuId);

}

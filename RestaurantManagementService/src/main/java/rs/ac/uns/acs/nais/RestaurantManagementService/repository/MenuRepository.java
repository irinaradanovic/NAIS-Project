package rs.ac.uns.acs.nais.RestaurantManagementService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Category;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Menu;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.MenuType;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Restaurant;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends Neo4jRepository<Menu, String> {

    @Query("MATCH (m:Menu) WHERE elementId(m) = $id " +
            "DETACH DELETE m")
    void deleteById(@Param("id") String id);

    // sve verzije istog menija
    @Query("MATCH (m:Menu {menuId: $menuId}) RETURN m")
    List<Menu> findByMenuId(Integer menuId);

    // najnovija verzija menija
    @Query("MATCH (m:Menu {menuId: $menuId}) " +
            "RETURN m ORDER BY m.version DESC LIMIT 1")
    Optional<Menu> findLatestVersionByMenuId(@Param("menuId") Integer menuId);

    List<Menu> findByType(MenuType type);

    @Query("MATCH (m:Menu) RETURN coalesce(max(m.menuId), 0)")
    Integer findMaxMenuId();


    @Query("MATCH (m:Menu), (c:Category) " +
            "WHERE elementId(m) = $menuId AND elementId(c) = $categoryId " +
            "CREATE (m)-[:HAS_CATEGORY {order: $order}]->(c)")
    void addCategoryToMenu(@Param("menuId") String menuId,
                           @Param("categoryId") String categoryId,
                           @Param("order") Integer order);

    @Query("MATCH (m:Menu)-[rel:HAS_CATEGORY]->(c:Category) " +
            "WHERE elementId(m) = $menuId AND elementId(c) = $categoryId " +
            "SET rel.order = $order")
    void updateCategoryOrder(@Param("menuId") String menuId,
                             @Param("categoryId") String categoryId,
                             @Param("order") Integer order);

    @Query("MATCH (m:Menu)-[rel:HAS_CATEGORY]->(c:Category) " +
            "WHERE elementId(m) = $menuId AND elementId(c) = $categoryId " +
            "DELETE rel")
    void removeCategoryFromMenu(@Param("menuId") String menuId,
                                @Param("categoryId") String categoryId);

    @Query("MATCH (m:Menu)-[rel:HAS_CATEGORY]->(c:Category) " +
            "WHERE elementId(m) = $menuId " +
            "RETURN c ORDER BY rel.order ASC")
    List<Category> findCategoriesByMenuId(@Param("menuId") String menuId);

    @Query("MATCH (m:Menu)-[:HAS_CATEGORY]->(c:Category) " +
            "WHERE elementId(m) = $menuId RETURN elementId(c)")
    List<String> findCategoryIdsByMenuId(@Param("menuId") String menuId);

}

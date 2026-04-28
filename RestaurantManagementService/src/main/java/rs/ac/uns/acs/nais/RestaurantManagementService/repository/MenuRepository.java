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
import java.util.UUID;

@Repository
public interface MenuRepository extends Neo4jRepository<Menu, UUID> {

    @Query("MATCH (m:Menu) WHERE m.id = $id " +
            "DETACH DELETE m")
    void deleteById(@Param("id") UUID id);

    // sve verzije istog menija
    @Query("MATCH (m:Menu {menuId: $menuId}) RETURN m")
    List<Menu> findByMenuId(Integer menuId);

    // najnovija verzija menija
    @Query("MATCH (m:Menu {menuId: $menuId}) " +
            "RETURN m ORDER BY m.version DESC LIMIT 1")
    Optional<Menu> findLatestVersionByMenuId(@Param("menuId") Integer menuId);


    @Query("MATCH (m:Menu) RETURN coalesce(max(m.menuId), 0)")
    Integer findMaxMenuId();


    @Query("MATCH (m:Menu), (c:Category) " +
            "WHERE m.id = $menuId AND c.id = $categoryId " +
            "CREATE (m)-[:HAS_CATEGORY {order: $order}]->(c)")
    void addCategoryToMenu(@Param("menuId") UUID menuId,
                           @Param("categoryId") UUID categoryId,
                           @Param("order") Integer order);

    @Query("MATCH (m:Menu)-[rel:HAS_CATEGORY]->(c:Category) " +
            "WHERE m.id = $menuId AND c.id = $categoryId " +
            "SET rel.order = $order")
    void updateCategoryOrder(@Param("menuId") UUID menuId,
                             @Param("categoryId") UUID categoryId,
                             @Param("order") Integer order);

    @Query("MATCH (m:Menu)-[rel:HAS_CATEGORY]->(c:Category) " +
            "WHERE m.id = $menuId AND rel.order >= $newOrder AND c.id <> $categoryId " +
            "SET rel.order = rel.order + 1")
    void shiftOrders(@Param("menuId") UUID menuId,
                     @Param("categoryId") UUID categoryId,
                     @Param("newOrder") Integer newOrder);

    @Query("MATCH (m:Menu)-[rel:HAS_CATEGORY]->(c:Category) " +
            "WHERE m.id = $menuId AND c.id = $categoryId " +
            "DELETE rel")
    void removeCategoryFromMenu(@Param("menuId") UUID menuId,
                                @Param("categoryId") UUID categoryId);

    @Query("MATCH (m:Menu)-[rel:HAS_CATEGORY]->(category:Category) " +
            "WHERE m.id = $menuId " +
            "RETURN category ORDER BY rel.order ASC")
    List<Category> findCategoriesByMenuId(@Param("menuId") UUID menuId);

    @Query("MATCH (m:Menu)-[:HAS_CATEGORY]->(c:Category) " +
            "WHERE m.id = $menuId RETURN c.id")
    List<UUID> findCategoryIdsByMenuId(@Param("menuId") UUID menuId);


    @Query("MATCH (m:Menu)-[r:HAS_CATEGORY]->(c:Category) " +
            "WHERE m.id = $id " +
            "RETURN m, r, c ")
    Menu findByIdWithCategories(UUID id);   // ovo koristimo za dobavljanje menija sa svim njenim kategorijama da bi mogli u novoj verziji da kopiramo sve te kategorije

}

package rs.ac.uns.acs.nais.RestaurantManagementService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Category;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Menu;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.MenuItem;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends Neo4jRepository<Category, String> {

    @Query("MATCH (c:Category) WHERE elementId(c) = $id " +
            "DETACH DELETE c")
    void deleteById(@Param("id") String id);

    @Query("MATCH (c:Category), (i:MenuItem) " +
            "WHERE elementId(c) = $categoryId AND elementId(i) = $itemId " +
            "CREATE (c)-[:INCLUDES_ITEM {discount: $discount, discountFrom: $discountFrom, discountTo: $discountTo}]->(i)")
    void addItemToCategory(@Param("categoryId") String categoryId,
                           @Param("itemId") String itemId,
                           @Param("discount") Double discount,
                           @Param("discountFrom") LocalDate discountFrom,
                           @Param("discountTo") LocalDate discountTo);

    @Query("MATCH (c:Category)-[rel:INCLUDES_ITEM]->(i:MenuItem) " +
            "WHERE elementId(c) = $categoryId AND elementId(i) = $itemId " +
            "SET rel.discount = $discount, rel.discountFrom = $discountFrom, rel.discountTo = $discountTo")
    void updateItemDiscount(@Param("categoryId") String categoryId,
                            @Param("itemId") String itemId,
                            @Param("discount") Double discount,
                            @Param("discountFrom") LocalDate discountFrom,
                            @Param("discountTo") LocalDate discountTo);

    @Query("MATCH (c:Category)-[rel:INCLUDES_ITEM]->(i:MenuItem) " +
            "WHERE elementId(c) = $categoryId AND elementId(i) = $itemId " +
            "DELETE rel")
    void removeItemFromCategory(@Param("categoryId") String categoryId,
                                @Param("itemId") String itemId);

    @Query("MATCH (c:Category)-[rel:INCLUDES_ITEM]->(i:MenuItem) " +
            "WHERE elementId(c) = $categoryId " +
            "RETURN i")
    List<MenuItem> findItemsByCategoryId(@Param("categoryId") String categoryId);

    @Query("MATCH (m:Menu)-[:HAS_CATEGORY]->(c:Category) " +
            "WHERE elementId(c) = $categoryId " +
            "RETURN m")
    List<Menu> findMenusByCategoryId(@Param("categoryId") String categoryId);

    @Query(" MATCH (m:Menu)-[rel:HAS_CATEGORY]->(c:Category)-[:INCLUDES_ITEM]->(i:MenuItem)" +
            " WHERE elementId(i) = $itemId" +
            "RETURN m")
    List<Menu> findMenusByItemId(@Param("itemId") String itemId);


}

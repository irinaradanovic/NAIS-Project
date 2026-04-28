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
import java.util.UUID;

@Repository
public interface CategoryRepository extends Neo4jRepository<Category, UUID> {

    @Query("MATCH (c:Category) WHERE c.id = $id " +
            "DETACH DELETE c")
    void deleteById(@Param("id") UUID id);

    @Query("MATCH (c:Category), (i:MenuItem) " +
            "WHERE c.id = $categoryId AND i.id = $itemId " +
            "CREATE (c)-[:INCLUDES_ITEM {discount: $discount, discountFrom: $discountFrom, discountTo: $discountTo}]->(i)")
    void addItemToCategory(@Param("categoryId") UUID categoryId,
                           @Param("itemId") UUID itemId,
                           @Param("discount") Double discount,
                           @Param("discountFrom") LocalDate discountFrom,
                           @Param("discountTo") LocalDate discountTo);

    @Query("MATCH (c:Category)-[rel:INCLUDES_ITEM]->(i:MenuItem) " +
            "WHERE c.id = $categoryId AND i.id = $itemId " +
            "SET rel.discount = $discount, rel.discountFrom = $discountFrom, rel.discountTo = $discountTo")
    void updateItemDiscount(@Param("categoryId") UUID categoryId,
                            @Param("itemId") UUID itemId,
                            @Param("discount") Double discount,
                            @Param("discountFrom") LocalDate discountFrom,
                            @Param("discountTo") LocalDate discountTo);

    @Query("MATCH (c:Category)-[rel:INCLUDES_ITEM]->(i:MenuItem) " +
            "WHERE c.id = $categoryId AND i.id = $itemId " +
            "DELETE rel")
    void removeItemFromCategory(@Param("categoryId") UUID categoryId,
                                @Param("itemId") UUID itemId);

    @Query("MATCH (c:Category)-[rel:INCLUDES_ITEM]->(menuItem:MenuItem) " +
            "WHERE c.id = $categoryId " +
            "RETURN menuItem")
    List<MenuItem> findItemsByCategoryId(@Param("categoryId") UUID categoryId);

    @Query("MATCH (m:Menu)-[:HAS_CATEGORY]->(c:Category) " +
            "WHERE c.id = $categoryId " +
            "RETURN m")
    List<Menu> findMenusByCategoryId(@Param("categoryId") UUID categoryId);

    @Query(" MATCH (m:Menu)-[rel:HAS_CATEGORY]->(c:Category)-[:INCLUDES_ITEM]->(i:MenuItem)" +
            " WHERE i.id = $itemId " +
            "RETURN m")
    List<Menu> findMenusByItemId(@Param("itemId") UUID itemId);


    @Query("MATCH (c:Category {id: $categoryId})-[rel:INCLUDES_ITEM]->(i:MenuItem) " +
            "SET rel.discount = $newDiscount, " +
            "    rel.discountFrom = date(), " +   // date() je danasnji datum
            "    rel.discountTo = date() + duration('P7D') " +   // 7 dana od danasnjeg datuma
            "RETURN count(rel)")
    void setWeeklyDiscountByCategory(@Param("categoryId") UUID categoryId,
                                        @Param("newDiscount") Double newDiscount);


}

package rs.ac.uns.acs.nais.RestaurantManagementService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.MenuItem;

import java.util.List;

@Repository
public interface MenuItemRepository extends Neo4jRepository<MenuItem, String> {

    @Query("MATCH (mI:MenuItem) WHERE elementId(mI) = $id " +
            "DETACH DELETE mI")
    void deleteById(@Param("id") String id);

    @Query("MATCH (mI:MenuItem) WHERE mi.timeMax <= $maxPrepTime RETURN mI")
    List<MenuItem> findByMaxPrepTime(Integer maxPrepTime);

    @Query("MATCH (i:MenuItem) WHERE elementId(i) = $id " +
            "SET i.price = $price RETURN i")
    MenuItem updatePrice(@Param("id") String id, @Param("price") Double price);
}

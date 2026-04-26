package rs.ac.uns.acs.nais.RestaurantManagementService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.MenuItem;

import java.util.List;

@Repository
public interface MenuItemRepository extends Neo4jRepository<MenuItem, Long> {

    List<MenuItem> findByCaloriesLessThanEqual(Integer maxCalories);

    List<MenuItem> findByPriceBetween(Double minPrice, Double maxPrice);

    @Query("MATCH (mI:MenuItem) WHERE mi.timeMax <= $maxPrepTime RETURN mI")
    List<MenuItem> findByMaxPrepTime(Integer maxPrepTime);

}

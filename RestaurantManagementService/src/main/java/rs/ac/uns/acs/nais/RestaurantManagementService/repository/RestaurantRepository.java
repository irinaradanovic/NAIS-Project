package rs.ac.uns.acs.nais.RestaurantManagementService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Restaurant;

import java.util.Optional;

@Repository
public interface RestaurantRepository extends Neo4jRepository<Restaurant, Long> {

    Optional<Restaurant> findByName(String name);

}

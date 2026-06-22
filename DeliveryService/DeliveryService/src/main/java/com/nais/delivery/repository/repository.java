package rs.ac.uns.acs.nais.DeliveryService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import rs.ac.uns.acs.nais.DeliveryService.model.neo4j.Courier;

import java.util.List;

public interface CourierRepository extends Neo4jRepository<Courier, String> {

    @Query("""
            MATCH (c:Courier)
            WHERE c.status='SLOBODAN'
            RETURN c
            """)
    List<Courier> findAvailableCouriers();

}
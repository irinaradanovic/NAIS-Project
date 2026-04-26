package rs.ac.uns.acs.nais.FinanceManagementService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.FinanceManagementService.model.Racun;

import java.util.List;

@Repository
public interface RacunRepository extends Neo4jRepository<Racun, String> {

    List<Racun> findByTip(String tip);

    List<Racun> findByIsPlacen(Boolean isPlacen);

    /** Svi neplaceni racuni kojima rok istice pre datog datuma */
    @Query("MATCH (r:Racun) " +
           "WHERE r.isPlacen = false AND r.rokPlacanja < $datum " +
           "RETURN r ORDER BY r.rokPlacanja ASC")
    List<Racun> findPrekoraceniRokovi(@Param("datum") String datum);
}

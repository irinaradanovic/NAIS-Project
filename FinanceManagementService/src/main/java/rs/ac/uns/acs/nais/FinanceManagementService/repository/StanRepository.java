package rs.ac.uns.acs.nais.FinanceManagementService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.FinanceManagementService.model.Stan;

import java.util.List;

@Repository
public interface StanRepository extends Neo4jRepository<Stan, String> {

    List<Stan> findByAdresa(String adresa);

    List<Stan> findByIsZauzet(Boolean isZauzet);

    /** Kreira PRIPADA_FONDU vezu između stana i fonda */
    @Query("MATCH (s:Stan {idOriginal: $stanId}) " +
           "MATCH (f:FinansijskiFond {idOriginal: $fondId}) " +
           "MERGE (s)-[:PRIPADA_FONDU]->(f)")
    void dodajFond(@Param("stanId") String stanId, @Param("fondId") String fondId);
}

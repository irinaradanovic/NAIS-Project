package rs.ac.uns.acs.nais.FinanceManagementService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.FinanceManagementService.model.Vlasnik;

import java.util.List;
import java.util.Map;

@Repository
public interface VlasnikRepository extends Neo4jRepository<Vlasnik, Long> {

    Vlasnik findByEmail(String email);

    // =====================================================================
    // CRUD operacije za POSEDUJE granu
    // =====================================================================

    /** Kreira POSEDUJE granu između vlasnika i stana */
    @Query("MATCH (v:Vlasnik {idOriginal: $vlasnikId}) " +
           "MATCH (s:Stan {idOriginal: $stanId}) " +
           "MERGE (v)-[:POSEDUJE]->(s)")
    void dodajStan(@Param("vlasnikId") Long vlasnikId, @Param("stanId") String stanId);

    /** Brise POSEDUJE granu između vlasnika i stana */
    @Query("MATCH (v:Vlasnik {idOriginal: $vlasnikId})-[r:POSEDUJE]->(s:Stan {idOriginal: $stanId}) " +
           "DELETE r")
    void ukloniStan(@Param("vlasnikId") Long vlasnikId, @Param("stanId") String stanId);
}

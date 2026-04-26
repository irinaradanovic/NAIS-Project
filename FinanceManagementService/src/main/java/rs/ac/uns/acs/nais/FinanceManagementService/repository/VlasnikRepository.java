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

    // =====================================================================
    // Slozeni upiti (MATCH + WHERE + WITH + agregacione funkcije)
    // =====================================================================

    /**
     * [Slozeni upit 1]
     * Za svakog vlasnika prikazuje ukupan broj stanova i prosecnu kvadraturu,
     * filtrirano na vlasnike koji imaju vise od 1 stana.
     */
    @Query("MATCH (v:Vlasnik)-[:POSEDUJE]->(s:Stan) " +
           "WHERE v.isAktivan = true " +
           "WITH v, COUNT(s) AS brojStanova, AVG(s.kvadratura) AS prosecnaKvadratura " +
           "WHERE brojStanova > 1 " +
           "RETURN v.ime AS ime, v.prezime AS prezime, " +
           "       brojStanova, ROUND(prosecnaKvadratura, 2) AS prosecnaKvadratura " +
           "ORDER BY brojStanova DESC")
    List<Map<String, Object>> vlasniciBrojStanovaIProsecnaKvadratura();

    /**
     * [Slozeni upit 2]
     * Vlasnici ciji stanari imaju neplacene racune — prikazuje ime vlasnika,
     * broj neplacenih racuna i ukupan neplacen iznos.
     */
    @Query("MATCH (v:Vlasnik)-[:POSEDUJE]->(s:Stan)<-[:STANUJE_U]-(st:Stanar)-[ir:IMA_RACUN]->(r:Racun) " +
           "WHERE r.isPlacen = false AND v.isAktivan = true " +
           "WITH v, COUNT(r) AS brojNeplacenih, SUM(r.iznos) AS ukupnoNeplaceno " +
           "WHERE brojNeplacenih > 0 " +
           "RETURN v.ime AS ime, v.prezime AS prezime, v.email AS email, " +
           "       brojNeplacenih, ROUND(ukupnoNeplaceno, 2) AS ukupnoNeplaceno " +
           "ORDER BY ukupnoNeplaceno DESC")
    List<Map<String, Object>> vlasniciBrojNeplacenihRacuna();
}

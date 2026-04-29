package rs.ac.uns.acs.nais.FinanceManagementService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.FinanceManagementService.model.Stanar;

import java.util.List;
import java.util.Map;

@Repository
public interface StanarRepository extends Neo4jRepository<Stanar, Long> {

    Stanar findByEmail(String email);

    // =====================================================================
    // CRUD operacije za STANUJE_U i IMA_RACUN grane
    // =====================================================================

    /** Kreira STANUJE_U granu između stanara i stana */
    @Query("MATCH (st:Stanar {idOriginal: $stanarId}) " +
           "MATCH (s:Stan {idOriginal: $stanId}) " +
           "MERGE (st)-[r:STANUJE_U]->(s) " +
           "ON CREATE SET r.datumUseljenja = $datumUseljenja, r.mesecnaKirija = $mesecnaKirija")
    void dodajStanovanje(@Param("stanarId") Long stanarId,
                         @Param("stanId") String stanId,
                         @Param("datumUseljenja") String datumUseljenja,
                         @Param("mesecnaKirija") double mesecnaKirija);

    /** Azurira mesecnu kiriju na STANUJE_U grani */
    @Query("MATCH (st:Stanar {idOriginal: $stanarId})-[r:STANUJE_U]->(s:Stan {idOriginal: $stanId}) " +
           "SET r.mesecnaKirija = $novaKirija")
    void azurirajKiriju(@Param("stanarId") Long stanarId,
                        @Param("stanId") String stanId,
                        @Param("novaKirija") double novaKirija);

    /** Brise STANUJE_U granu (iseljavanje stanara) */
    @Query("MATCH (st:Stanar {idOriginal: $stanarId})-[r:STANUJE_U]->(s:Stan {idOriginal: $stanId}) " +
           "DELETE r")
    void ukloniStanovanje(@Param("stanarId") Long stanarId, @Param("stanId") String stanId);

    /** Kreira IMA_RACUN granu između stanara i racuna */
    @Query("MATCH (st:Stanar {idOriginal: $stanarId}) " +
           "MATCH (r:Racun {idOriginal: $racunId}) " +
           "MERGE (st)-[ir:IMA_RACUN]->(r) " +
           "ON CREATE SET ir.datumPlacanja = null, ir.isNaVreme = false")
    void dodajRacun(@Param("stanarId") Long stanarId, @Param("racunId") String racunId);

    /** Oznacava racun kao placen i belezi datum placanja */
    @Query("MATCH (st:Stanar {idOriginal: $stanarId})-[ir:IMA_RACUN]->(r:Racun {idOriginal: $racunId}) " +
           "SET ir.datumPlacanja = $datumPlacanja, ir.isNaVreme = $isNaVreme, r.isPlacen = true")
    void platiRacun(@Param("stanarId") Long stanarId,
                    @Param("racunId") String racunId,
                    @Param("datumPlacanja") String datumPlacanja,
                    @Param("isNaVreme") boolean isNaVreme);

}

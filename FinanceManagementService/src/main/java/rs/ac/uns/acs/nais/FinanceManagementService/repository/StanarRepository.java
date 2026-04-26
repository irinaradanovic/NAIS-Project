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

    // =====================================================================
    // Slozeni upiti (MATCH + WHERE + WITH + agregacione funkcije)
    // =====================================================================

    /**
     * [Slozeni upit 3]
     * Stanari koji kasne sa placanjem — prikazuje stanara, broj neplacenih racuna
     * i ukupan dug, sortirano po dugu.
     */
    @Query("MATCH (st:Stanar)-[ir:IMA_RACUN]->(r:Racun) " +
           "WHERE r.isPlacen = false AND st.isAktivan = true " +
           "WITH st, COUNT(r) AS brojNeplacenih, SUM(r.iznos) AS ukupanDug " +
           "WHERE brojNeplacenih > 0 " +
           "RETURN st.ime AS ime, st.prezime AS prezime, st.email AS email, " +
           "       brojNeplacenih, ROUND(ukupanDug, 2) AS ukupanDug " +
           "ORDER BY ukupanDug DESC")
    List<Map<String, Object>> stanariSaDugovima();

    /**
     * [Slozeni upit 4]
     * Prosecna mesecna kirija po adresi zgrade — grupise stanove po adresi
     * i racuna prosecnu kiriju i broj aktivnih stanara.
     */
    @Query("MATCH (st:Stanar)-[r:STANUJE_U]->(s:Stan) " +
           "WHERE st.isAktivan = true " +
           "WITH s.adresa AS adresa, AVG(r.mesecnaKirija) AS prosecnaKirija, COUNT(st) AS brojStanara " +
           "WHERE brojStanara > 0 " +
           "RETURN adresa, ROUND(prosecnaKirija, 2) AS prosecnaKirija, brojStanara " +
           "ORDER BY prosecnaKirija DESC")
    List<Map<String, Object>> prosecnaKirijaPoAdresi();

    /**
     * [Slozeni upit 5]
     * Preporuka povecanja kirije: stanari koji placaju ispod proseka kirija
     * u svojoj zgradi — potencijalni kandidati za usklađivanje.
     */
    @Query("MATCH (st:Stanar)-[r:STANUJE_U]->(s:Stan) " +
           "WHERE st.isAktivan = true " +
           "WITH s.adresa AS adresa, AVG(r.mesecnaKirija) AS prosecnaKirijaNaAdresi " +
           "MATCH (st2:Stanar)-[r2:STANUJE_U]->(s2:Stan) " +
           "WHERE s2.adresa = adresa AND r2.mesecnaKirija < prosecnaKirijaNaAdresi " +
           "WITH st2, r2.mesecnaKirija AS trenutnaKirija, prosecnaKirijaNaAdresi, " +
           "     ROUND(prosecnaKirijaNaAdresi - r2.mesecnaKirija, 2) AS razlika " +
           "WHERE razlika > 0 " +
           "RETURN st2.ime AS ime, st2.prezime AS prezime, " +
           "       trenutnaKirija, ROUND(prosecnaKirijaNaAdresi, 2) AS prosecnaKirija, razlika " +
           "ORDER BY razlika DESC")
    List<Map<String, Object>> stanariIspodProsecneKirije();
}

package rs.ac.uns.acs.nais.FinanceManagementService.service.impl;

import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.FinanceManagementService.model.Vlasnik;
import rs.ac.uns.acs.nais.FinanceManagementService.repository.VlasnikRepository;
import rs.ac.uns.acs.nais.FinanceManagementService.service.IVlasnikService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VlasnikService implements IVlasnikService {

    private final VlasnikRepository vlasnikRepository;
    private final Neo4jClient neo4jClient;

    public VlasnikService(VlasnikRepository vlasnikRepository, Neo4jClient neo4jClient) {
        this.vlasnikRepository = vlasnikRepository;
        this.neo4jClient = neo4jClient;
    }

    @Override
    public List<Vlasnik> findAll() {
        return vlasnikRepository.findAll();
    }

    @Override
    public Vlasnik addVlasnik(Vlasnik vlasnik) {
        vlasnik.setIsAktivan(true);
        return vlasnikRepository.save(vlasnik);
    }

    @Override
    public boolean deleteVlasnik(String email) {
        Vlasnik v = vlasnikRepository.findByEmail(email);
        if (v != null) {
            v.setIsAktivan(false);
            vlasnikRepository.save(v);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateEmail(String stariEmail, String noviEmail) {
        Vlasnik v = vlasnikRepository.findByEmail(stariEmail);
        if (v != null) {
            v.setEmail(noviEmail);
            vlasnikRepository.save(v);
            return true;
        }
        return false;
    }

    @Override
    public void dodajStan(Long vlasnikId, String stanId) {
        vlasnikRepository.dodajStan(vlasnikId, stanId);
    }

    @Override
    public void ukloniStan(Long vlasnikId, String stanId) {
        vlasnikRepository.ukloniStan(vlasnikId, stanId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> vlasniciBrojStanovaIProsecnaKvadratura() {
        String query = """
                MATCH (v:Vlasnik)-[:POSEDUJE]->(s:Stan)
                WHERE v.isAktivan = true
                WITH v, COUNT(s) AS brojStanova, AVG(s.kvadratura) AS prosecnaKvadratura
                WHERE brojStanova > 1
                RETURN v.ime AS ime, v.prezime AS prezime,
                       brojStanova, ROUND(prosecnaKvadratura, 2) AS prosecnaKvadratura
                ORDER BY brojStanova DESC
                """;

        return neo4jClient.query(query)
                .fetchAs(Map.class)
                .mappedBy((typeSystem, record) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("ime", record.get("ime").asString());
                    row.put("prezime", record.get("prezime").asString());
                    row.put("brojStanova", record.get("brojStanova").asLong());
                    row.put("prosecnaKvadratura", record.get("prosecnaKvadratura").asDouble());
                    return row;
                })
                .all()
                .stream()
                .map(m -> (Map<String, Object>) m)
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> vlasniciBrojNeplacenihRacuna() {
        String query = """
                MATCH (v:Vlasnik)-[:POSEDUJE]->(s:Stan)<-[:STANUJE_U]-(st:Stanar)-[ir:IMA_RACUN]->(r:Racun)
                WHERE r.isPlacen = false AND v.isAktivan = true
                WITH v, COUNT(r) AS brojNeplacenih, SUM(r.iznos) AS ukupnoNeplaceno
                WHERE brojNeplacenih > 0
                RETURN v.ime AS ime, v.prezime AS prezime, v.email AS email,
                       brojNeplacenih, ROUND(ukupnoNeplaceno, 2) AS ukupnoNeplaceno
                ORDER BY ukupnoNeplaceno DESC
                """;

        return neo4jClient.query(query)
                .fetchAs(Map.class)
                .mappedBy((typeSystem, record) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("ime", record.get("ime").asString());
                    row.put("prezime", record.get("prezime").asString());
                    row.put("email", record.get("email").asString());
                    row.put("brojNeplacenih", record.get("brojNeplacenih").asLong());
                    row.put("ukupnoNeplaceno", record.get("ukupnoNeplaceno").asDouble());
                    return row;
                })
                .all()
                .stream()
                .map(m -> (Map<String, Object>) m)
                .collect(Collectors.toList());
    }
}
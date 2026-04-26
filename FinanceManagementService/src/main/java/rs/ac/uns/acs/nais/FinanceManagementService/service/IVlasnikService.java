package rs.ac.uns.acs.nais.FinanceManagementService.service;

import rs.ac.uns.acs.nais.FinanceManagementService.model.Vlasnik;

import java.util.List;
import java.util.Map;

public interface IVlasnikService {
    List<Vlasnik> findAll();
    Vlasnik addVlasnik(Vlasnik vlasnik);
    boolean deleteVlasnik(String email);
    boolean updateEmail(String stariEmail, String noviEmail);
    void dodajStan(Long vlasnikId, String stanId);
    void ukloniStan(Long vlasnikId, String stanId);
    List<Map<String, Object>> vlasniciBrojStanovaIProsecnaKvadratura();
    List<Map<String, Object>> vlasniciBrojNeplacenihRacuna();
}

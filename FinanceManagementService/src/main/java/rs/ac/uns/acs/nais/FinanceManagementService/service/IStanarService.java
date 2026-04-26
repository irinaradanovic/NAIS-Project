package rs.ac.uns.acs.nais.FinanceManagementService.service;

import rs.ac.uns.acs.nais.FinanceManagementService.dto.PlatiRacunDTO;
import rs.ac.uns.acs.nais.FinanceManagementService.dto.StanovanjeDTO;
import rs.ac.uns.acs.nais.FinanceManagementService.model.Stanar;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IStanarService {
    List<Stanar> findAll();
    Stanar addStanar(Stanar stanar);
    boolean deleteStanar(String email);
    boolean updateEmail(String stariEmail, String noviEmail);
    void dodajStanovanje(StanovanjeDTO dto);
    void azurirajKiriju(Long stanarId, String stanId, double novaKirija);
    void ukloniStanovanje(Long stanarId, String stanId);
    void dodajRacun(Long stanarId, String racunId);
    void platiRacun(PlatiRacunDTO dto);
    List<Map<String, Object>> stanariSaDugovima();
    List<Map<String, Object>> prosecnaKirijaPoAdresi();
    List<Map<String, Object>> stanariIspodProsecneKirije();
    byte[] exportIzvestaj() throws IOException;
}

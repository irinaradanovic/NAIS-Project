package rs.ac.uns.acs.nais.FinanceManagementService.service.impl;

import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.FinanceManagementService.model.Vlasnik;
import rs.ac.uns.acs.nais.FinanceManagementService.repository.VlasnikRepository;
import rs.ac.uns.acs.nais.FinanceManagementService.service.IVlasnikService;

import java.util.List;
import java.util.Map;

@Service
public class VlasnikService implements IVlasnikService {

    private final VlasnikRepository vlasnikRepository;

    public VlasnikService(VlasnikRepository vlasnikRepository) {
        this.vlasnikRepository = vlasnikRepository;
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
    public List<Map<String, Object>> vlasniciBrojStanovaIProsecnaKvadratura() {
        return vlasnikRepository.vlasniciBrojStanovaIProsecnaKvadratura();
    }

    @Override
    public List<Map<String, Object>> vlasniciBrojNeplacenihRacuna() {
        return vlasnikRepository.vlasniciBrojNeplacenihRacuna();
    }
}

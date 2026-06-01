package rs.ac.uns.acs.nais.FinanceManagementService.controller.influx;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.FinanceManagementService.model.influx.FondTransakcija;
import rs.ac.uns.acs.nais.FinanceManagementService.model.influx.PlacanjeDogadjaj;
import rs.ac.uns.acs.nais.FinanceManagementService.service.influx.FinanceInfluxService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/influx")
public class FinanceInfluxController {

    private final FinanceInfluxService service;

    public FinanceInfluxController(FinanceInfluxService service) {
        this.service = service;
    }

    // PlacanjeDogadjaj CRUD

    @PostMapping("/placanja")
    public ResponseEntity<String> savePlacanje(@RequestBody PlacanjeDogadjaj dogadjaj) {
        service.savePlacanje(dogadjaj);
        return ResponseEntity.ok("Dogadjaj placanja uspesno sacuvan.");
    }

    @GetMapping("/placanja")
    public ResponseEntity<List<PlacanjeDogadjaj>> getAllPlacanja() {
        return ResponseEntity.ok(service.getAllPlacanja());
    }

    @GetMapping("/placanja/stanar/{stanarId}")
    public ResponseEntity<List<PlacanjeDogadjaj>> getPlacanjaByStanar(@PathVariable String stanarId) {
        return ResponseEntity.ok(service.getPlacanjaByStanarId(stanarId));
    }

    @GetMapping("/placanja/tip/{tipRacuna}")
    public ResponseEntity<List<PlacanjeDogadjaj>> getPlacanjaByTip(@PathVariable String tipRacuna) {
        return ResponseEntity.ok(service.getPlacanjaByTip(tipRacuna));
    }

    @DeleteMapping("/placanja/stanar/{stanarId}")
    public ResponseEntity<String> deletePlacanjaStanara(@PathVariable String stanarId) {
        boolean ok = service.deletePlacanjaByStanarId(stanarId);
        return ok
                ? ResponseEntity.ok("Placanja stanara " + stanarId + " obrisana.")
                : ResponseEntity.internalServerError().body("Greska pri brisanju.");
    }

    // FondTransakcija CRUD

    @PostMapping("/fond-transakcije")
    public ResponseEntity<String> saveFondTransakcija(@RequestBody FondTransakcija transakcija) {
        service.saveFondTransakcija(transakcija);
        return ResponseEntity.ok("Transakcija fonda uspesno sacuvana.");
    }

    @GetMapping("/fond-transakcije")
    public ResponseEntity<List<FondTransakcija>> getAllFondTransakcije() {
        return ResponseEntity.ok(service.getAllFondTransakcije());
    }

    @GetMapping("/fond-transakcije/fond/{fondId}")
    public ResponseEntity<List<FondTransakcija>> getTransakcijeByFond(@PathVariable String fondId) {
        return ResponseEntity.ok(service.getFondTransakcijeByFondId(fondId));
    }

    @GetMapping("/fond-transakcije/tip/{tipTransakcije}")
    public ResponseEntity<List<FondTransakcija>> getTransakcijeByTip(@PathVariable String tipTransakcije) {
        return ResponseEntity.ok(service.getFondTransakcijeByTip(tipTransakcije));
    }

    @DeleteMapping("/fond-transakcije/fond/{fondId}")
    public ResponseEntity<String> deleteFondTransakcije(@PathVariable String fondId) {
        boolean ok = service.deleteFondTransakcijeByFondId(fondId);
        return ok
                ? ResponseEntity.ok("Transakcije fonda " + fondId + " obrisane.")
                : ResponseEntity.internalServerError().body("Greska pri brisanju.");
    }

    // Slozeni upiti

    @GetMapping("/upiti/ukupno-po-tipu")
    public ResponseEntity<List<Map<String, Object>>> ukupnoPoTipuRacuna(
            @RequestParam(defaultValue = "365") int dani) {
        return ResponseEntity.ok(service.ukupnoPoTipuRacuna(dani));
    }

    @GetMapping("/upiti/kasnjenja-stanara")
    public ResponseEntity<List<Map<String, Object>>> prosecnoKasnjenjePoPlacanjeStanara() {
        return ResponseEntity.ok(service.prosecnoKasnjenjePoPlacanjeStanara());
    }

    @GetMapping("/upiti/mesecni-prihodi-na-vreme")
    public ResponseEntity<List<Map<String, Object>>> mesecniPrihodiNaVreme() {
        return ResponseEntity.ok(service.mesecniPrihodiNaVreme());
    }

    @GetMapping("/upiti/bilans-fondova")
    public ResponseEntity<List<Map<String, Object>>> bilansFondova() {
        return ResponseEntity.ok(service.bilansPOFondu());
    }

    @GetMapping("/upiti/mesecni-obrt-fondova")
    public ResponseEntity<List<Map<String, Object>>> mesecniObrtFondova() {
        return ResponseEntity.ok(service.mesecniObrtFondova());
    }

    @GetMapping("/upiti/visoke-isplate")
    public ResponseEntity<List<Map<String, Object>>> fondoviSaVisokimIsplatama(
            @RequestParam(defaultValue = "50000") double prag) {
        return ResponseEntity.ok(service.fondoviSaVisokimIsplatama(prag));
    }
}
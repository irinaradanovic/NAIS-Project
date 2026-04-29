package rs.ac.uns.acs.nais.FinanceManagementService.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.FinanceManagementService.model.Vlasnik;
import rs.ac.uns.acs.nais.FinanceManagementService.service.impl.VlasnikService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/vlasnici")
public class VlasnikController {

    private final VlasnikService vlasnikService;

    public VlasnikController(VlasnikService vlasnikService) {
        this.vlasnikService = vlasnikService;
    }

    @GetMapping
    public ResponseEntity<List<Vlasnik>> findAll() {
        return new ResponseEntity<>(vlasnikService.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Vlasnik> addVlasnik(@RequestBody Vlasnik vlasnik) {
        return new ResponseEntity<>(vlasnikService.addVlasnik(vlasnik), HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteVlasnik(@RequestParam String email) {
        if (vlasnikService.deleteVlasnik(email)) return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping
    public ResponseEntity<Void> updateEmail(
            @RequestParam String stariEmail,
            @RequestParam String noviEmail) {
        if (vlasnikService.updateEmail(stariEmail, noviEmail)) return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/dodaj-stan")
    public ResponseEntity<Void> dodajStan(
            @RequestParam Long vlasnikId,
            @RequestParam String stanId) {
        vlasnikService.dodajStan(vlasnikId, stanId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/ukloni-stan")
    public ResponseEntity<Void> ukloniStan(
            @RequestParam Long vlasnikId,
            @RequestParam String stanId) {
        vlasnikService.ukloniStan(vlasnikId, stanId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // =====================================================================
    // Slozeni upiti
    // =====================================================================

    /** [Upit 1] Vlasnici sa brojem stanova i prosecnom kvadraturom */
    @GetMapping("/statistika-stanovi")
    public ResponseEntity<List<Map<String, Object>>> vlasniciBrojStanovaIProsecnaKvadratura() {
        return new ResponseEntity<>(vlasnikService.vlasniciBrojStanovaIProsecnaKvadratura(), HttpStatus.OK);
    }

    /** [Upit 2] Vlasnici ciji stanari imaju neplacene racune */
    @GetMapping("/neplaceni-racuni")
    public ResponseEntity<List<Map<String, Object>>> vlasniciBrojNeplacenihRacuna() {
        return new ResponseEntity<>(vlasnikService.vlasniciBrojNeplacenihRacuna(), HttpStatus.OK);
    }
}

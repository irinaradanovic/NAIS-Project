package rs.ac.uns.acs.nais.FinanceManagementService.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.FinanceManagementService.dto.PlatiRacunDTO;
import rs.ac.uns.acs.nais.FinanceManagementService.dto.StanovanjeDTO;
import rs.ac.uns.acs.nais.FinanceManagementService.model.Stanar;
import rs.ac.uns.acs.nais.FinanceManagementService.service.impl.StanarService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stanari")
public class StanarController {

    private final StanarService stanarService;

    public StanarController(StanarService stanarService) {
        this.stanarService = stanarService;
    }

    @GetMapping
    public ResponseEntity<List<Stanar>> findAll() {
        return new ResponseEntity<>(stanarService.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Stanar> addStanar(@RequestBody Stanar stanar) {
        return new ResponseEntity<>(stanarService.addStanar(stanar), HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteStanar(@RequestParam String email) {
        if (stanarService.deleteStanar(email)) return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping
    public ResponseEntity<Void> updateEmail(
            @RequestParam String stariEmail,
            @RequestParam String noviEmail) {
        if (stanarService.updateEmail(stariEmail, noviEmail)) return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/stanovanje")
    public ResponseEntity<Void> dodajStanovanje(@RequestBody StanovanjeDTO dto) {
        stanarService.dodajStanovanje(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/kirija")
    public ResponseEntity<Void> azurirajKiriju(
            @RequestParam Long stanarId,
            @RequestParam String stanId,
            @RequestParam double novaKirija) {
        stanarService.azurirajKiriju(stanarId, stanId, novaKirija);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/stanovanje")
    public ResponseEntity<Void> ukloniStanovanje(
            @RequestParam Long stanarId,
            @RequestParam String stanId) {
        stanarService.ukloniStanovanje(stanarId, stanId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/dodaj-racun")
    public ResponseEntity<Void> dodajRacun(
            @RequestParam Long stanarId,
            @RequestParam String racunId) {
        stanarService.dodajRacun(stanarId, racunId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/plati-racun")
    public ResponseEntity<Void> platiRacun(@RequestBody PlatiRacunDTO dto) {
        stanarService.platiRacun(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // =====================================================================
    // Slozeni upiti
    // =====================================================================

    /** [Upit 3] Stanari sa neplacenim racunima i ukupnim dugom */
    @GetMapping("/dugovi")
    public ResponseEntity<List<Map<String, Object>>> stanariSaDugovima() {
        return new ResponseEntity<>(stanarService.stanariSaDugovima(), HttpStatus.OK);
    }

    /** [Upit 4] Prosecna mesecna kirija po adresi zgrade */
    @GetMapping("/prosecna-kirija")
    public ResponseEntity<List<Map<String, Object>>> prosecnaKirijaPoAdresi() {
        return new ResponseEntity<>(stanarService.prosecnaKirijaPoAdresi(), HttpStatus.OK);
    }

    /** [Upit 5] Stanari koji placaju ispod prosecne kirije u zgradi */
    @GetMapping("/ispod-proseka")
    public ResponseEntity<List<Map<String, Object>>> stanariIspodProsecneKirije() {
        return new ResponseEntity<>(stanarService.stanariIspodProsecneKirije(), HttpStatus.OK);
    }

    @GetMapping(value = "/export-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportPdf() {
        try {
            byte[] pdf = stanarService.exportIzvestaj();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "izvestaj-dugovi.pdf");
            return ResponseEntity.ok().headers(headers).body(pdf);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

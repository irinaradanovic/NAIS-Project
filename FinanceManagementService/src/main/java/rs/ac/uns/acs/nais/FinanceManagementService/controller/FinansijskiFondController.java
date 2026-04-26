package rs.ac.uns.acs.nais.FinanceManagementService.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.FinanceManagementService.model.FinansijskiFond;
import rs.ac.uns.acs.nais.FinanceManagementService.repository.FinansijskiFondRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/fondovi")
public class FinansijskiFondController {

    private final FinansijskiFondRepository fondRepository;

    public FinansijskiFondController(FinansijskiFondRepository fondRepository) {
        this.fondRepository = fondRepository;
    }

    @GetMapping
    public ResponseEntity<List<FinansijskiFond>> findAll() {
        return new ResponseEntity<>(fondRepository.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<FinansijskiFond> addFond(@RequestBody FinansijskiFond fond) {
        fond.setIsAktivan(true);
        return new ResponseEntity<>(fondRepository.save(fond), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFond(@PathVariable String id) {
        Optional<FinansijskiFond> f = fondRepository.findById(id);
        if (f.isPresent()) {
            f.get().setIsAktivan(false);
            fondRepository.save(f.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}/doprinos")
    public ResponseEntity<Void> updateDoprinos(@PathVariable String id, @RequestParam double noviDoprinos) {
        Optional<FinansijskiFond> f = fondRepository.findById(id);
        if (f.isPresent()) {
            f.get().setMesecniDoprinos(noviDoprinos);
            fondRepository.save(f.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/aktivni")
    public ResponseEntity<List<FinansijskiFond>> aktivniFondovi() {
        return new ResponseEntity<>(fondRepository.findByIsAktivan(true), HttpStatus.OK);
    }
}

package rs.ac.uns.acs.nais.FinanceManagementService.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.FinanceManagementService.model.Racun;
import rs.ac.uns.acs.nais.FinanceManagementService.repository.RacunRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/racuni")
public class RacunController {

    private final RacunRepository racunRepository;

    public RacunController(RacunRepository racunRepository) {
        this.racunRepository = racunRepository;
    }

    @GetMapping
    public ResponseEntity<List<Racun>> findAll() {
        return new ResponseEntity<>(racunRepository.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Racun> addRacun(@RequestBody Racun racun) {
        racun.setIsPlacen(false);
        return new ResponseEntity<>(racunRepository.save(racun), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRacun(@PathVariable String id) {
        if (racunRepository.existsById(id)) {
            racunRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}/iznos")
    public ResponseEntity<Void> updateIznos(@PathVariable String id, @RequestParam double noviIznos) {
        Optional<Racun> r = racunRepository.findById(id);
        if (r.isPresent()) {
            r.get().setIznos(noviIznos);
            racunRepository.save(r.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/neplaceni")
    public ResponseEntity<List<Racun>> neplaceni() {
        return new ResponseEntity<>(racunRepository.findByIsPlacen(false), HttpStatus.OK);
    }

    @GetMapping("/prekoraceni")
    public ResponseEntity<List<Racun>> prekoraceni(@RequestParam String datum) {
        return new ResponseEntity<>(racunRepository.findPrekoraceniRokovi(datum), HttpStatus.OK);
    }
}

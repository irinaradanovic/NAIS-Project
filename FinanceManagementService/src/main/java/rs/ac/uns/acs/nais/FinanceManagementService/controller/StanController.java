package rs.ac.uns.acs.nais.FinanceManagementService.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.FinanceManagementService.model.Stan;
import rs.ac.uns.acs.nais.FinanceManagementService.repository.StanRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/stanovi")
public class StanController {

    private final StanRepository stanRepository;

    public StanController(StanRepository stanRepository) {
        this.stanRepository = stanRepository;
    }

    @GetMapping
    public ResponseEntity<List<Stan>> findAll() {
        return new ResponseEntity<>(stanRepository.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Stan> addStan(@RequestBody Stan stan) {
        stan.setIsZauzet(false);
        return new ResponseEntity<>(stanRepository.save(stan), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStan(@PathVariable String id) {
        if (stanRepository.existsById(id)) {
            stanRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}/kvadratura")
    public ResponseEntity<Void> updateKvadratura(@PathVariable String id, @RequestParam double novaKvadratura) {
        Optional<Stan> s = stanRepository.findById(id);
        if (s.isPresent()) {
            s.get().setKvadratura(novaKvadratura);
            stanRepository.save(s.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/slobodni")
    public ResponseEntity<List<Stan>> slobodniStanovi() {
        return new ResponseEntity<>(stanRepository.findByIsZauzet(false), HttpStatus.OK);
    }

    @PostMapping("/{id}/fond")
    public ResponseEntity<Void> dodajFond(@PathVariable String id, @RequestParam String fondId) {
        stanRepository.dodajFond(id, fondId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

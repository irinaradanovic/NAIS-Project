package rs.ac.uns.acs.nais.FinanceManagementService.service.influx;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.FinanceManagementService.model.influx.FondTransakcija;
import rs.ac.uns.acs.nais.FinanceManagementService.model.influx.PlacanjeDogadjaj;
import rs.ac.uns.acs.nais.FinanceManagementService.repository.influx.FondTransakcijaInfluxRepository;
import rs.ac.uns.acs.nais.FinanceManagementService.repository.influx.PlacanjeInfluxRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@Slf4j
public class FinanceInfluxService implements CommandLineRunner {

    private final PlacanjeInfluxRepository placanjeRepo;
    private final FondTransakcijaInfluxRepository fondRepo;

    public FinanceInfluxService(PlacanjeInfluxRepository placanjeRepo,
                                FondTransakcijaInfluxRepository fondRepo) {
        this.placanjeRepo = placanjeRepo;
        this.fondRepo = fondRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        List<PlacanjeDogadjaj> existing = placanjeRepo.findAll();
        if (!existing.isEmpty()) {
            System.out.println("[InfluxDB] Podaci vec postoje. Preskacemo seeding.");
            return;
        }
        System.out.println("[InfluxDB] Pokrecemo seeding podataka...");
        seedPlacanjaData();
        seedFondTransakcijeData();
        System.out.println("[InfluxDB] Seeding zavrsen!");
    }

    private void seedPlacanjaData() {
        Random random = new Random(42);
        String[][] stanari = {
                {"1", "pera@example.com"}, {"2", "mika@example.com"},
                {"3", "zika@example.com"}, {"4", "ana@example.com"},
                {"5", "maja@example.com"}, {"6", "jovan@example.com"},
                {"7", "nikola@example.com"}, {"8", "jovana@example.com"},
                {"9", "stefan@example.com"}, {"10", "milena@example.com"}
        };
        String[] tipoviRacuna = {"Struja", "Voda", "Grejanje", "Komunalije", "Kirija"};
        String[] stanIds = {"stan-1","stan-2","stan-3","stan-4","stan-5",
                "stan-6","stan-7","stan-8","stan-9","stan-10"};
        List<PlacanjeDogadjaj> placanja = new ArrayList<>();
        Instant sada = Instant.now();
        for (int i = 0; i < 1200; i++) {
            String[] stanar = stanari[random.nextInt(stanari.length)];
            String tipRacuna = tipoviRacuna[random.nextInt(tipoviRacuna.length)];
            String stanId = stanIds[random.nextInt(stanIds.length)];
            double iznos = switch (tipRacuna) {
                case "Kirija"     -> 25000 + random.nextDouble() * 25000;
                case "Grejanje"   -> 3000  + random.nextDouble() * 7000;
                case "Struja"     -> 2000  + random.nextDouble() * 5000;
                case "Voda"       -> 800   + random.nextDouble() * 1200;
                default           -> 500   + random.nextDouble() * 1500;
            };
            boolean naVremeFlag = random.nextDouble() < 0.70;
            double daniKasnjenja = naVremeFlag ? 0.0 : (1 + random.nextInt(30));
            Instant timestamp = sada
                    .minus(random.nextInt(360), ChronoUnit.DAYS)
                    .minus(random.nextInt(24), ChronoUnit.HOURS)
                    .minus(random.nextInt(60), ChronoUnit.MINUTES);
            placanja.add(new PlacanjeDogadjaj(
                    stanar[0], stanar[1], tipRacuna, stanId,
                    Math.round(iznos * 100.0) / 100.0,
                    naVremeFlag ? 1.0 : 0.0,
                    daniKasnjenja,
                    timestamp
            ));
        }
        placanjeRepo.saveAll(placanja);
        System.out.println("[InfluxDB] Upisano " + placanja.size() + " dogadjaja placanja.");
    }

    private void seedFondTransakcijeData() {
        Random random = new Random(99);
        String[][] fondovi = {
                {"fond-1", "Fond za odrzavanje"},
                {"fond-2", "Rezervni fond"},
                {"fond-3", "Fond za hitne popravke"},
                {"fond-4", "Fond za investicije"}
        };
        String[] stanIds = {"stan-1","stan-2","stan-3","stan-4","stan-5",
                "stan-6","stan-7","stan-8","stan-9","stan-10"};
        List<FondTransakcija> transakcije = new ArrayList<>();
        Instant sada = Instant.now();
        double[] iznosiFondova = {120000.0, 85000.0, 45000.0, 200000.0};
        for (int i = 0; i < 1200; i++) {
            int fondIdx = random.nextInt(fondovi.length);
            String[] fond = fondovi[fondIdx];
            String stanId = stanIds[random.nextInt(stanIds.length)];
            String tip = random.nextDouble() < 0.6 ? "UPLATA" : "ISPLATA";
            double iznos = tip.equals("UPLATA")
                    ? 500  + random.nextDouble() * 5000
                    : 200  + random.nextDouble() * 3000;
            if (tip.equals("UPLATA")) {
                iznosiFondova[fondIdx] += iznos;
            } else {
                iznosiFondova[fondIdx] = Math.max(0, iznosiFondova[fondIdx] - iznos);
            }
            Instant timestamp = sada
                    .minus(random.nextInt(360), ChronoUnit.DAYS)
                    .minus(random.nextInt(24), ChronoUnit.HOURS)
                    .minus(random.nextInt(60), ChronoUnit.MINUTES);
            transakcije.add(new FondTransakcija(
                    fond[0], fond[1], tip, stanId,
                    Math.round(iznos * 100.0) / 100.0,
                    Math.round(iznosiFondova[fondIdx] * 100.0) / 100.0,
                    timestamp
            ));
        }
        fondRepo.saveAll(transakcije);
        System.out.println("[InfluxDB] Upisano " + transakcije.size() + " transakcija fondova.");
    }

    // CRUD

    @CacheEvict(value = {"ukupnoPoTipu", "mesecniPrihodi", "kasnjenja", "bilansFondova", "mesecniObrt", "visokePlate"}, allEntries = true)
    public void savePlacanje(PlacanjeDogadjaj dogadjaj) {
        log.info("[CACHE EVICT] Clearing cache zbog novog placanja.");
        placanjeRepo.save(dogadjaj);
    }

    public List<PlacanjeDogadjaj> getAllPlacanja() { return placanjeRepo.findAll(); }
    public List<PlacanjeDogadjaj> getPlacanjaByStanarId(String id) { return placanjeRepo.findByStanarId(id); }
    public List<PlacanjeDogadjaj> getPlacanjaByTip(String tip) { return placanjeRepo.findByTipRacuna(tip); }

    @CacheEvict(value = {"ukupnoPoTipu", "mesecniPrihodi", "kasnjenja"}, allEntries = true)
    public boolean deletePlacanjaByStanarId(String id) {
        log.info("[CACHE EVICT] Clearing cache zbog brisanja placanja stanara: {}", id);
        return placanjeRepo.deleteByStanarId(id);
    }

    @CacheEvict(value = {"bilansFondova", "mesecniObrt", "visokePlate"}, allEntries = true)
    public void saveFondTransakcija(FondTransakcija t) {
        log.info("[CACHE EVICT] Clearing cache zbog nove fond transakcije.");
        fondRepo.save(t);
    }

    public List<FondTransakcija> getAllFondTransakcije() { return fondRepo.findAll(); }
    public List<FondTransakcija> getFondTransakcijeByFondId(String id) { return fondRepo.findByFondId(id); }
    public List<FondTransakcija> getFondTransakcijeByTip(String tip) { return fondRepo.findByTipTransakcije(tip); }

    @CacheEvict(value = {"bilansFondova", "mesecniObrt", "visokePlate"}, allEntries = true)
    public boolean deleteFondTransakcijeByFondId(String id) {
        log.info("[CACHE EVICT] Clearing cache zbog brisanja transakcija fonda: {}", id);
        return fondRepo.deleteByFondId(id);
    }

    // Složeni upiti sa @Cacheable

    @Cacheable(value = "ukupnoPoTipu", key = "#dani")
    public List<Map<String, Object>> ukupnoPoTipuRacuna(int dani) {
        log.info("[CACHE] Querying InfluxDB za ukupnoPoTipuRacuna, dani={}", dani);
        return placanjeRepo.ukupnoPoTipuRacuna(dani);
    }

    @Cacheable(value = "kasnjenja", key = "'sviStanari'")
    public List<Map<String, Object>> prosecnoKasnjenjePoPlacanjeStanara() {
        log.info("[CACHE] Querying InfluxDB za prosecnoKasnjenjePoPlacanjeStanara");
        return placanjeRepo.prosecnoKasnjenjePoPlacanjeStanara();
    }

    @Cacheable(value = "mesecniPrihodi", key = "'mesecniPrihodi'")
    public List<Map<String, Object>> mesecniPrihodiNaVreme() {
        log.info("[CACHE] Querying InfluxDB za mesecniPrihodiNaVreme");
        return placanjeRepo.mesecniPrihodiNaVreme();
    }

    @Cacheable(value = "bilansFondova", key = "'bilans'")
    public List<Map<String, Object>> bilansPOFondu() {
        log.info("[CACHE] Querying InfluxDB za bilansPOFondu");
        return fondRepo.bilansPOFondu();
    }

    @Cacheable(value = "mesecniObrt", key = "'mesecniObrt'")
    public List<Map<String, Object>> mesecniObrtFondova() {
        log.info("[CACHE] Querying InfluxDB za mesecniObrtFondova");
        return fondRepo.mesecniObrtFondova();
    }

    @Cacheable(value = "visokePlate", key = "#prag")
    public List<Map<String, Object>> fondoviSaVisokimIsplatama(double prag) {
        log.info("[CACHE] Querying InfluxDB za fondoviSaVisokimIsplatama, prag={}", prag);
        return fondRepo.fondoviSaVisokimIsplatama(prag);
    }
}
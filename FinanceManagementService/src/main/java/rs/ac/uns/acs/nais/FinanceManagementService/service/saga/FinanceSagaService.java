package rs.ac.uns.acs.nais.FinanceManagementService.service.saga;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.FinanceManagementService.config.RabbitMQConfig;
import rs.ac.uns.acs.nais.FinanceManagementService.dto.saga.PokreniDostavuSagaRequestDTO;
import rs.ac.uns.acs.nais.FinanceManagementService.dto.saga.SagaDeliveryRequestEventDTO;
import rs.ac.uns.acs.nais.FinanceManagementService.dto.saga.SagaDeliveryResponseEventDTO;
import rs.ac.uns.acs.nais.FinanceManagementService.model.FinansijskiFond;
import rs.ac.uns.acs.nais.FinanceManagementService.model.influx.SagaFinansijskaTransakcija;
import rs.ac.uns.acs.nais.FinanceManagementService.model.saga.SagaDostaveState;
import rs.ac.uns.acs.nais.FinanceManagementService.model.saga.SagaStatus;
import rs.ac.uns.acs.nais.FinanceManagementService.repository.FinansijskiFondRepository;
import rs.ac.uns.acs.nais.FinanceManagementService.repository.influx.SagaFinansijskaTransakcijaInfluxRepository;
import rs.ac.uns.acs.nais.FinanceManagementService.repository.saga.SagaStateRedisRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class FinanceSagaService {

    private final FinansijskiFondRepository fondRepository;
    private final SagaStateRedisRepository sagaStateRepository;
    private final SagaFinansijskaTransakcijaInfluxRepository sagaInfluxRepository;
    private final RabbitTemplate rabbitTemplate;

    public FinanceSagaService(FinansijskiFondRepository fondRepository,
                              SagaStateRedisRepository sagaStateRepository,
                              SagaFinansijskaTransakcijaInfluxRepository sagaInfluxRepository,
                              RabbitTemplate rabbitTemplate) {
        this.fondRepository = fondRepository;
        this.sagaStateRepository = sagaStateRepository;
        this.sagaInfluxRepository = sagaInfluxRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Saga Korak 1 (Finansije): provera sredstava, naplata iz fonda,
     * upis transakcije i ASINHRONO slanje zahteva servisu za Dostavu.
     */
    public SagaDostaveState pokreniSagu(PokreniDostavuSagaRequestDTO request) {

        Optional<FinansijskiFond> fondOpt = fondRepository.findById(request.getFondId());
        if (fondOpt.isEmpty()) {
            throw new IllegalArgumentException("FinansijskiFond sa id-om " + request.getFondId() + " ne postoji.");
        }

        FinansijskiFond fond = fondOpt.get();
        String sagaId = UUID.randomUUID().toString();
        Instant sada = Instant.now();

        SagaDostaveState state = new SagaDostaveState();
        state.setSagaId(sagaId);
        state.setFondId(fond.getIdOriginal());
        state.setAdresaDostave(request.getAdresaDostave());
        state.setIznos(request.getIznos());
        state.setOpis(request.getOpis());
        state.setVremePokretanja(sada);
        state.setVremeAzuriranja(sada);

        // Provera dovoljnosti sredstava - ne ulazimo u sagu ako fond nema dovoljno novca
        if (fond.getUkupanIznos() < request.getIznos()) {
            state.setStatus(SagaStatus.ODBIJENA_NEDOVOLJNO_SREDSTAVA);
            sagaStateRepository.save(state);

            sagaInfluxRepository.save(new SagaFinansijskaTransakcija(
                    sagaId, fond.getIdOriginal(), "NAPLATA",
                    SagaStatus.ODBIJENA_NEDOVOLJNO_SREDSTAVA.name(),
                    request.getIznos(), fond.getUkupanIznos(),
                    "Nedovoljno sredstava na fondu", sada
            ));

            log.warn("[SAGA {}] Odbijen zahtev - nedovoljno sredstava na fondu {}.", sagaId, fond.getIdOriginal());
            return state;
        }

        // Korak 1: naplata iz fonda (lokalna transakcija)
        double novoStanje = fond.getUkupanIznos() - request.getIznos();
        fond.setUkupanIznos(novoStanje);
        fondRepository.save(fond);

        state.setStatus(SagaStatus.FOND_NAPLACEN);
        sagaStateRepository.save(state);

        sagaInfluxRepository.save(new SagaFinansijskaTransakcija(
                sagaId, fond.getIdOriginal(), "NAPLATA",
                SagaStatus.FOND_NAPLACEN.name(),
                request.getIznos(), novoStanje, null, sada
        ));

        log.info("[SAGA {}] Fond {} naplacen za {}. Novo stanje: {}.", sagaId, fond.getIdOriginal(),
                request.getIznos(), novoStanje);

        // Asinhrono slanje zahteva servisu za Dostavu (ne cekamo odgovor!)
        SagaDeliveryRequestEventDTO event = new SagaDeliveryRequestEventDTO(
                sagaId, fond.getIdOriginal(), request.getAdresaDostave(),
                request.getIznos(), request.getOpis(), sada
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SAGA_EXCHANGE,
                RabbitMQConfig.DELIVERY_REQUEST_ROUTING_KEY,
                event
        );

        log.info("[SAGA {}] Zahtev za dostavu poslat asinhrono na red '{}'.",
                sagaId, RabbitMQConfig.DELIVERY_REQUEST_QUEUE);

        return state;
    }

    /**
     * Obrada odgovora od servisa za Dostavu (poziva se iz RabbitListener-a).
     * Uspeh -> saga zavrsena. Neuspeh -> KOMPENZACIJA (vracanje novca na fond).
     */
    public void obradiOdgovorDostave(SagaDeliveryResponseEventDTO response) {

        Optional<SagaDostaveState> stateOpt = sagaStateRepository.findById(response.getSagaId());
        if (stateOpt.isEmpty()) {
            log.warn("[SAGA {}] Nepoznata saga - poruka se ignorise.", response.getSagaId());
            return;
        }

        SagaDostaveState state = stateOpt.get();

        // Idempotentnost - ako je saga vec u terminalnom stanju, ignorisemo duplikat poruke
        if (state.getStatus() == SagaStatus.ZAVRSENA || state.getStatus() == SagaStatus.KOMPENZOVANA) {
            log.info("[SAGA {}] Saga je vec u terminalnom stanju ({}), poruka se ignorise.",
                    state.getSagaId(), state.getStatus());
            return;
        }

        Instant sada = Instant.now();

        if ("USPESNO".equalsIgnoreCase(response.getStatus())) {
            state.setStatus(SagaStatus.ZAVRSENA);
            state.setDostavljacId(response.getDostavljacId());
            state.setVremeAzuriranja(sada);
            sagaStateRepository.save(state);

            log.info("[SAGA {}] Dostava uspesno dodeljena (dostavljac: {}). Saga ZAVRSENA.",
                    state.getSagaId(), response.getDostavljacId());
            return;
        }

        // KOMPENZACIJA: nema slobodnog dostavljaca / adresa nedostupna -> vracamo novac na fond
        kompenzujTransakciju(state, response.getRazlogNeuspeha());
    }

    private void kompenzujTransakciju(SagaDostaveState state, String razlogNeuspeha) {

        Optional<FinansijskiFond> fondOpt = fondRepository.findById(state.getFondId());
        if (fondOpt.isEmpty()) {
            log.error("[SAGA {}] Fond {} ne postoji prilikom kompenzacije!", state.getSagaId(), state.getFondId());
            return;
        }

        FinansijskiFond fond = fondOpt.get();
        double novoStanje = fond.getUkupanIznos() + state.getIznos();
        fond.setUkupanIznos(novoStanje);
        fondRepository.save(fond);

        Instant sada = Instant.now();

        state.setStatus(SagaStatus.KOMPENZOVANA);
        state.setRazlogNeuspeha(razlogNeuspeha);
        state.setVremeAzuriranja(sada);
        sagaStateRepository.save(state);

        sagaInfluxRepository.save(new SagaFinansijskaTransakcija(
                state.getSagaId(), fond.getIdOriginal(), "STORNO",
                SagaStatus.KOMPENZOVANA.name(),
                state.getIznos(), novoStanje, razlogNeuspeha, sada
        ));

        log.warn("[SAGA {}] KOMPENZACIJA izvrsena - novac vracen na fond {}. Razlog: {}. Novo stanje: {}.",
                state.getSagaId(), fond.getIdOriginal(), razlogNeuspeha, novoStanje);
    }

    public Optional<SagaDostaveState> getStatus(String sagaId) {
        return sagaStateRepository.findById(sagaId);
    }
}
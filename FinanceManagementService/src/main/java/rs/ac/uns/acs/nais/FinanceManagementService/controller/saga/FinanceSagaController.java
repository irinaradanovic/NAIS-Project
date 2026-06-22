package rs.ac.uns.acs.nais.FinanceManagementService.controller.saga;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.FinanceManagementService.config.RabbitMQConfig;
import rs.ac.uns.acs.nais.FinanceManagementService.dto.saga.PokreniDostavuSagaRequestDTO;
import rs.ac.uns.acs.nais.FinanceManagementService.dto.saga.SagaDeliveryResponseEventDTO;
import rs.ac.uns.acs.nais.FinanceManagementService.model.saga.SagaDostaveState;
import rs.ac.uns.acs.nais.FinanceManagementService.service.saga.FinanceSagaService;

@RestController
@RequestMapping("/api/saga/dostava")
public class FinanceSagaController {

    private final FinanceSagaService financeSagaService;
    private final RabbitTemplate rabbitTemplate;

    public FinanceSagaController(FinanceSagaService financeSagaService, RabbitTemplate rabbitTemplate) {
        this.financeSagaService = financeSagaService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping
    public ResponseEntity<SagaDostaveState> pokreniSagu(@RequestBody PokreniDostavuSagaRequestDTO request) {
        SagaDostaveState state = financeSagaService.pokreniSagu(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(state);
    }

    @GetMapping("/{sagaId}")
    public ResponseEntity<SagaDostaveState> getStatus(@PathVariable String sagaId) {
        return financeSagaService.getStatus(sagaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{sagaId}/simulate-response")
    public ResponseEntity<String> simulirajOdgovorDostave(
            @PathVariable String sagaId,
            @RequestParam String status, // "USPESNO" ili "NEUSPESNO"
            @RequestParam(required = false) String dostavljacId,
            @RequestParam(required = false) String razlogNeuspeha) {

        SagaDeliveryResponseEventDTO event = new SagaDeliveryResponseEventDTO();
        event.setSagaId(sagaId);
        event.setStatus(status);
        event.setDostavljacId(dostavljacId);
        event.setRazlogNeuspeha(razlogNeuspeha);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SAGA_EXCHANGE,
                RabbitMQConfig.DELIVERY_RESPONSE_ROUTING_KEY,
                event
        );

        return ResponseEntity.ok("Simulirani odgovor poslat za sagaId=" + sagaId);
    }
}
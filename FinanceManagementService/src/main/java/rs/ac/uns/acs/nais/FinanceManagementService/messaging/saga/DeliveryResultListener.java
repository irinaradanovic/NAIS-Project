package rs.ac.uns.acs.nais.FinanceManagementService.messaging.saga;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import rs.ac.uns.acs.nais.FinanceManagementService.config.RabbitMQConfig;
import rs.ac.uns.acs.nais.FinanceManagementService.dto.saga.SagaDeliveryResponseEventDTO;
import rs.ac.uns.acs.nais.FinanceManagementService.service.saga.FinanceSagaService;

@Component
public class DeliveryResultListener {

    private final FinanceSagaService financeSagaService;

    public DeliveryResultListener(FinanceSagaService financeSagaService) {
        this.financeSagaService = financeSagaService;
    }

    @RabbitListener(queues = RabbitMQConfig.FINANCE_RESPONSE_QUEUE)
    public void handleDeliveryResult(SagaDeliveryResponseEventDTO response) {
        System.out.println("[SAGA] Primljen asinhroni odgovor od servisa za Dostavu za sagaId=" + response.getSagaId());
        try {
            financeSagaService.obradiOdgovorDostave(response);
        } catch (Exception e) {
            System.err.println("[SAGA] Greska prilikom obrade odgovora dostave: " + e.getMessage());
        }
    }
}
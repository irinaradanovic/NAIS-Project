package rs.ac.uns.acs.nais.MockDeliveryService.listener;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import rs.ac.uns.acs.nais.MockDeliveryService.config.RabbitMQConfig;
import rs.ac.uns.acs.nais.MockDeliveryService.service.DeliveryAssignmentService;

@Component
public class DeliveryAssignmentListener {

    private final DeliveryAssignmentService deliveryAssignmentService;

    public DeliveryAssignmentListener(DeliveryAssignmentService deliveryAssignmentService) {
        this.deliveryAssignmentService = deliveryAssignmentService;
    }

    @RabbitListener(queues = RabbitMQConfig.DELIVERY_REQUEST_QUEUE)
    public void handleDeliveryRequest(Message message) throws Exception {
        deliveryAssignmentService.processDeliveryRequest(message);
    }
}
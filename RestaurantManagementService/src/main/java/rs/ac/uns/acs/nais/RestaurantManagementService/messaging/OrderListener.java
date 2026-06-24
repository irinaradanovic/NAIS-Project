package rs.ac.uns.acs.nais.RestaurantManagementService.messaging;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.client.RestTemplate;
import rs.ac.uns.acs.nais.RestaurantManagementService.config.RabbitMQConfig;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.RestaurantSagaRequest;
import rs.ac.uns.acs.nais.RestaurantManagementService.repository.MenuItemRepository;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.SagaResponseDTO;

import java.util.HashMap;
import java.util.Map;

@Component
public class OrderListener {
    private final MenuItemRepository menuItemRepository;
    private final RabbitTemplate rabbitTemplate;
    private final RestTemplate restTemplate;

    public OrderListener(MenuItemRepository menuItemRepository, RabbitTemplate rabbitTemplate) {
        this.menuItemRepository = menuItemRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.restTemplate = new RestTemplate();
    }

    @RabbitListener(queues = "restaurant.order.queue")
    public void handleNewOrderEvent(RestaurantSagaRequest request) {
        System.out.println("Stigla nova porudzbina preko RabbitMQ za stavku: " + request.getItemId());
        boolean inventoryUpdated = false;

        try {
            validateRequest(request);

            String itemId = menuItemRepository.processInventoryUpdate(request.getItemId(), request.getQuantity());
            if (itemId == null ) {
                throw new RuntimeException("Stavka " + request.getItemId() + " nije dostupna ili nema dovoljno na stanju");
            }
            inventoryUpdated = true;

            String details = menuItemRepository.findRestaurantAndCategoryDetails(request.getItemId());
            String[] parts = details.split(",");
            String restaurantId = parts[0];
            String restaurantName = parts[1];
            String categoryName = parts[2];

            // pakovanje podataka za influxdb
            Map<String, Object> influxBody = new HashMap<>();
            influxBody.put("restaurantId", restaurantId);
            influxBody.put("restaurantName", restaurantName);
            influxBody.put("menuItemId", request.getItemId());
            influxBody.put("categoryName", categoryName);
            influxBody.put("actualDurationMinutes", 0.0);

            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, "restaurant.timeseries.log", influxBody);
            System.out.println("Saga korak uspešno izvrsen: Neo4j azuriran, Influx event poslat u RabbitMQ.");

            SagaResponseDTO successResponse = new SagaResponseDTO(request.getOrderId(), "SUCCESS", "Kuhinja potvrdila porudzbinu.");
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.RESTAURANT_RESPONSE_KEY, successResponse);

        } catch (Exception e) {
            System.out.println("Greska u Sagi na strani restorana: " + e.getMessage());
            if (inventoryUpdated) {
                System.out.println("Kompenzacija: Vracanje stavke " + request.getItemId() + " nazad u prethodno stanje.");
                menuItemRepository.compensateInventoryUpdate(request.getItemId(), request.getQuantity());
            }

            SagaResponseDTO failedResponse = new SagaResponseDTO(request.getOrderId(), "FAILED", e.getMessage());
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.RESTAURANT_RESPONSE_KEY, failedResponse);
        }
    }

    private void validateRequest(RestaurantSagaRequest request) {
        if (request == null || request.getOrderId() == null) {
            throw new IllegalArgumentException("orderId je obavezan");
        }
        if (request.getItemId() == null || request.getItemId().isBlank()) {
            throw new IllegalArgumentException("itemId je obavezan");
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("quantity mora biti veći od nule");
        }
    }
}

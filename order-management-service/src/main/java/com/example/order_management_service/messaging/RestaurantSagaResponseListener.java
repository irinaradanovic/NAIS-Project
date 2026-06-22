package com.example.order_management_service.messaging;

import com.example.order_management_service.configuration.RabbitMQConfig;
import com.example.order_management_service.dto.SagaResponseDto;
import com.example.order_management_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestaurantSagaResponseListener {

    private final OrderService orderService;

    @RabbitListener(queues = RabbitMQConfig.ORDER_RESPONSE_QUEUE)
    public void handleRestaurantResponse(SagaResponseDto response) {
        orderService.handleRestaurantResponse(response);
    }
}

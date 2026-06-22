package com.example.order_management_service.service;

import com.example.order_management_service.model.Order;
import com.example.order_management_service.model.OrderSagaEvent;
import com.example.order_management_service.repository.OrderSagaEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderSagaEventService {

    private final OrderSagaEventRepository orderSagaEventRepository;

    public void log(Order order, String eventType, String status, String reason, Integer itemCount) {
        String orderId = order.getId() != null ? order.getId().toString() : null;
        log(orderId, order.getRestaurantId(), eventType, status, reason, itemCount);
    }

    public void log(UUID orderId, String restaurantId, String eventType, String status, String reason, Integer itemCount) {
        log(orderId != null ? orderId.toString() : null, restaurantId, eventType, status, reason, itemCount);
    }

    private void log(String orderId, String restaurantId, String eventType, String status, String reason, Integer itemCount) {
        try {
            orderSagaEventRepository.save(new OrderSagaEvent(
                    orderId,
                    restaurantId,
                    eventType,
                    status,
                    reason,
                    itemCount,
                    Instant.now()
            ));
        } catch (Exception e) {
            log.warn("Failed to write order saga event {} for order {}", eventType, orderId, e);
        }
    }
}

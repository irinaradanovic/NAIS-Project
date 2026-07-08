package com.example.order_management_service.service;

import com.example.order_management_service.configuration.RabbitMQConfig;
import com.example.order_management_service.dto.OrderItemRequestDto;
import com.example.order_management_service.dto.OrderMetricRequestDto;
import com.example.order_management_service.dto.OrderRequestDto;
import com.example.order_management_service.dto.RestaurantSagaRequestDto;
import com.example.order_management_service.dto.SagaResponseDto;
import com.example.order_management_service.model.Order;
import com.example.order_management_service.repository.ArticleRepository;
import com.example.order_management_service.repository.InvoiceRepository;
import com.example.order_management_service.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private OrderSagaEventService orderSagaEventService;

    @Mock
    private OrderMetricService orderMetricService;

    @InjectMocks
    private OrderService orderService;

    private UUID orderId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
    }

    @Test
    void createStoresPendingOrderItemsAndPublishesValidationEvent() {
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(orderId);
            return order;
        });

        OrderRequestDto request = new OrderRequestDto(
                LocalDateTime.of(2026, 6, 22, 15, 30),
                "CONFIRMED",
                "DELIVERY",
                "Bulevar Oslobodjenja 10",
                "res-kfc-ns-3333",
                "cust-test-001",
                List.of(new OrderItemRequestDto("item-zinger-v3", 2))
        );

        orderService.create(request);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals("PENDING_VALIDATION", orderCaptor.getValue().getStatus());
        assertEquals("res-kfc-ns-3333", orderCaptor.getValue().getRestaurantId());
        assertEquals("cust-test-001", orderCaptor.getValue().getCustomerId());

        verify(orderRepository).linkCustomerToOrder("cust-test-001", orderId);
        verify(orderRepository).addMenuItemToOrder(orderId, "item-zinger-v3", 2);
        ArgumentCaptor<OrderMetricRequestDto> metricCaptor = ArgumentCaptor.forClass(OrderMetricRequestDto.class);
        verify(orderMetricService).create(metricCaptor.capture());
        assertEquals(orderId.toString(), metricCaptor.getValue().getOrderId());
        assertEquals("PENDING_VALIDATION", metricCaptor.getValue().getStatus());
        assertEquals(2, metricCaptor.getValue().getItemCount());

        ArgumentCaptor<RestaurantSagaRequestDto> eventCaptor = ArgumentCaptor.forClass(RestaurantSagaRequestDto.class);
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.EXCHANGE),
                eq(RabbitMQConfig.RESTAURANT_ORDER_QUEUE_ROUTING_KEY),
                eventCaptor.capture()
        );

        RestaurantSagaRequestDto event = eventCaptor.getValue();
        assertEquals(orderId, event.getOrderId());
        assertEquals("item-zinger-v3", event.getItemId());
        assertEquals(2, event.getQuantity());
    }

    @Test
    void createRejectsMultipleItemsUntilRestaurantSupportsMultiItemValidation() {
        OrderRequestDto request = new OrderRequestDto(
                LocalDateTime.of(2026, 6, 22, 15, 30),
                "CONFIRMED",
                "DELIVERY",
                "Bulevar Oslobodjenja 10",
                "res-kfc-ns-3333",
                "cust-test-001",
                List.of(
                        new OrderItemRequestDto("item-hot-wings-6kom", 2),
                        new OrderItemRequestDto("item-zinger-v3", 1)
                )
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.create(request)
        );

        assertEquals("Exactly one order item is supported until Restaurant supports multi-item validation", exception.getMessage());
        verifyNoMoreInteractions(orderRepository, rabbitTemplate);
    }

    @Test
    void handleRestaurantSuccessConfirmsPendingOrder() {
        Order pending = pendingOrder();
        Order confirmed = pendingOrder();
        confirmed.setStatus("CONFIRMED");

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(pending));
        when(orderRepository.updateStatus(orderId, "CONFIRMED")).thenReturn(confirmed);

        orderService.handleRestaurantResponse(new SagaResponseDto(orderId, "SUCCESS", "Available"));

        verify(orderRepository).updateStatus(orderId, "CONFIRMED");
        verify(orderSagaEventService).log(confirmed, "VALIDATION_CONFIRMED", "CONFIRMED", "Available", null);
    }

    @Test
    void handleRestaurantFailureRejectsPendingOrder() {
        Order pending = pendingOrder();
        Order rejected = pendingOrder();
        rejected.setStatus("REJECTED");

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(pending));
        when(orderRepository.updateStatus(orderId, "REJECTED")).thenReturn(rejected);

        orderService.handleRestaurantResponse(new SagaResponseDto(orderId, "FAILED", "Item unavailable"));

        verify(orderRepository).updateStatus(orderId, "REJECTED");
        verify(orderSagaEventService).log(rejected, "VALIDATION_REJECTED", "REJECTED", "Item unavailable", null);
    }

    @Test
    void handleRestaurantResponseIgnoresAlreadyFinalizedOrder() {
        Order confirmed = pendingOrder();
        confirmed.setStatus("CONFIRMED");
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(confirmed));

        orderService.handleRestaurantResponse(new SagaResponseDto(orderId, "FAILED", "Late response"));

        verify(orderSagaEventService).log(confirmed, "VALIDATION_RESPONSE_IGNORED", "CONFIRMED", "Order is already finalized", null);
        verifyNoMoreInteractions(rabbitTemplate);
    }

    private Order pendingOrder() {
        Order order = new Order();
        order.setId(orderId);
        order.setStatus("PENDING_VALIDATION");
        order.setRestaurantId("res-kfc-ns-3333");
        assertNotNull(order.getId());
        return order;
    }
}

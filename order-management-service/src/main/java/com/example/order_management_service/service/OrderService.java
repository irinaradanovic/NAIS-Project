package com.example.order_management_service.service;

import com.example.order_management_service.dto.OrderRequestDto;
import com.example.order_management_service.dto.OrderResponseDto;
import com.example.order_management_service.dto.RestaurantSagaRequestDto;
import com.example.order_management_service.dto.SagaResponseDto;
import com.example.order_management_service.model.Article;
import com.example.order_management_service.model.Invoice;
import com.example.order_management_service.model.Order;
import com.example.order_management_service.configuration.RabbitMQConfig;
import com.example.order_management_service.repository.ArticleRepository;
import com.example.order_management_service.repository.InvoiceRepository;
import com.example.order_management_service.repository.OrderRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final String PENDING_VALIDATION = "PENDING_VALIDATION";
    private static final String CONFIRMED = "CONFIRMED";
    private static final String REJECTED = "REJECTED";
    private static final String SAGA_SUCCESS = "SUCCESS";

    @Autowired private OrderRepository orderRepository;
    @Autowired private ArticleRepository articleRepository;
    @Autowired private InvoiceRepository invoiceRepository;
    @Autowired private RabbitTemplate rabbitTemplate;
    @Autowired private OrderSagaEventService orderSagaEventService;

    public OrderResponseDto create(OrderRequestDto dto) {
        validateCreateRequest(dto);

        Order order = new Order();
        order.setCreationDate(dto.getCreationDate() != null ? dto.getCreationDate() : LocalDateTime.now());
        order.setStatus(PENDING_VALIDATION);
        order.setOrderType(dto.getOrderType());
        order.setAddress(dto.getAddress());
        order.setRestaurantId(dto.getRestaurantId());

        Order saved = orderRepository.save(order);
        orderSagaEventService.log(saved, "ORDER_CREATED_PENDING", PENDING_VALIDATION, "Order created and waiting for restaurant validation", dto.getItems().size());

        dto.getItems().forEach(item ->
                orderRepository.addMenuItemToOrder(saved.getId(), item.getMenuItemId(), item.getQuantity())
        );

        RestaurantSagaRequestDto event = new RestaurantSagaRequestDto(
                saved.getId(),
                dto.getItems().get(0).getMenuItemId(),
                dto.getItems().get(0).getQuantity()
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.RESTAURANT_ORDER_QUEUE_ROUTING_KEY,
                event
        );
        orderSagaEventService.log(saved, "VALIDATION_REQUEST_SENT", PENDING_VALIDATION, "Restaurant validation request sent", dto.getItems().size());

        return mapToDTO(saved);
    }

    public void handleRestaurantResponse(SagaResponseDto response) {
        if (response == null || response.getOrderId() == null) {
            orderSagaEventService.log((UUID) null, null, "VALIDATION_RESPONSE_IGNORED", null, "Missing orderId in restaurant response", null);
            return;
        }

        Order order = orderRepository.findById(response.getOrderId()).orElse(null);
        if (order == null) {
            orderSagaEventService.log(response.getOrderId(), null, "VALIDATION_RESPONSE_IGNORED", response.getStatus(), "Order not found: " + response.getReason(), null);
            return;
        }

        if (!PENDING_VALIDATION.equals(order.getStatus())) {
            orderSagaEventService.log(order, "VALIDATION_RESPONSE_IGNORED", order.getStatus(), "Order is already finalized", null);
            return;
        }

        String newStatus = SAGA_SUCCESS.equalsIgnoreCase(response.getStatus()) ? CONFIRMED : REJECTED;
        String eventType = CONFIRMED.equals(newStatus) ? "VALIDATION_CONFIRMED" : "VALIDATION_REJECTED";
        Order updated = orderRepository.updateStatus(order.getId(), newStatus);
        orderSagaEventService.log(updated, eventType, newStatus, response.getReason(), null);
    }

    public List<OrderResponseDto> getAll() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public OrderResponseDto getById(UUID id) {
        return mapToDTO(getEntityById(id));
    }

    public OrderResponseDto update(UUID id, OrderRequestDto dto) {
        Order existing = getEntityById(id);
        if (dto.getCreationDate() != null) existing.setCreationDate(dto.getCreationDate());
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());
        if (dto.getOrderType() != null) existing.setOrderType(dto.getOrderType());
        if (dto.getAddress() != null) existing.setAddress(dto.getAddress());

        Order saved = orderRepository.save(existing);
        return mapToDTO(saved);
    }

    public void delete(UUID id) {
        orderRepository.deleteById(id);
    }

    public void addArticle(UUID orderId, UUID articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));
        articleRepository.save(article);
        orderRepository.addArticleToOrder(orderId, articleId);
    }

    public void removeArticle(UUID orderId, UUID articleId) {
        orderRepository.removeArticleFromOrder(orderId, articleId);
    }

    public List<Article> getArticles(UUID orderId) {
        return articleRepository.findByOrderId(orderId);
    }

    public void addInvoice(UUID orderId, UUID invoiceId) {
        invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        orderRepository.addInvoiceToOrder(orderId, invoiceId);
    }

    public List<Invoice> getInvoices(UUID orderId) {
        return invoiceRepository.findByOrderId(orderId);
    }



    private Order getEntityById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    private OrderResponseDto mapToDTO(Order order) {
        return new OrderResponseDto(
                order.getId(),
                order.getCreationDate(),
                order.getStatus(),
                order.getOrderType(),
                order.getAddress(),
                order.getRestaurantId()
        );
    }

    private void validateCreateRequest(OrderRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Order request is required");
        }
        if (dto.getRestaurantId() == null || dto.getRestaurantId().isBlank()) {
            throw new IllegalArgumentException("restaurantId is required");
        }
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("At least one order item is required");
        }
        if (dto.getItems().size() != 1) {
            throw new IllegalArgumentException("Exactly one order item is supported until Restaurant supports multi-item validation");
        }
        dto.getItems().forEach(item -> {
            if (item == null || item.getMenuItemId() == null || item.getMenuItemId().isBlank()) {
                throw new IllegalArgumentException("menuItemId is required for every order item");
            }
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("quantity must be greater than zero for every order item");
            }
        });
    }
}

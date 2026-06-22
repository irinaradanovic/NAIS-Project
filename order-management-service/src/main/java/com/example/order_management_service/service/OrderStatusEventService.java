package com.example.order_management_service.service;

import com.example.order_management_service.dto.OrderStatusEventRequestDto;
import com.example.order_management_service.model.OrderStatusEvent;
import com.example.order_management_service.repository.OrderStatusEventRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class OrderStatusEventService {

    private final OrderStatusEventRepository orderStatusEventRepository;

    public OrderStatusEventService(OrderStatusEventRepository orderStatusEventRepository) {
        this.orderStatusEventRepository = orderStatusEventRepository;
    }

    public void create(OrderStatusEventRequestDto dto) {
        OrderStatusEvent event = new OrderStatusEvent(
                dto.getOrderId(),
                dto.getStatus(),
                dto.getChannel(),
                dto.getPaymentMethod(),
                dto.getProcessingSeconds(),
                dto.getTotalAmount(),
                dto.getTime() != null ? dto.getTime() : Instant.now()
        );
        orderStatusEventRepository.save(event);
    }

    public List<OrderStatusEvent> getAll() {
        return orderStatusEventRepository.findAll();
    }

    public boolean deleteByOrderId(String orderId) {
        return orderStatusEventRepository.deleteByOrderId(orderId);
    }
}

package com.example.order_management_service.service;

import com.example.order_management_service.dto.OrderArticleRequestDto;
import com.example.order_management_service.dto.OrderArticleResponseDto;
import com.example.order_management_service.repository.OrderArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderArticleService {

    private final OrderArticleRepository orderArticleRepository;

    public OrderArticleResponseDto create(OrderArticleRequestDto dto) {
        OrderArticleRepository.OrderArticleRecord record =
                orderArticleRepository.create(dto.getOrderId(), dto.getArticleId(), dto.getQuantity());
        return mapToDto(record);
    }

    public List<OrderArticleResponseDto> getAll() {
        return orderArticleRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public OrderArticleResponseDto getById(Long id) {
        OrderArticleRepository.OrderArticleRecord record = orderArticleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OrderArticle not found"));
        return mapToDto(record);
    }

    public OrderArticleResponseDto update(Long id, OrderArticleRequestDto dto) {
        Integer quantity = dto.getQuantity();
        OrderArticleRepository.OrderArticleRecord record = orderArticleRepository.updateQuantity(id, quantity);
        return mapToDto(record);
    }

    public void delete(Long id) {
        orderArticleRepository.deleteById(id);
    }

    private OrderArticleResponseDto mapToDto(OrderArticleRepository.OrderArticleRecord record) {
        return new OrderArticleResponseDto(
                record.getId(),
                record.getOrderId(),
                record.getArticleId(),
                record.getQuantity()
        );
    }
}
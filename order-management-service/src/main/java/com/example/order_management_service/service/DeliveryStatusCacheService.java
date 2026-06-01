package com.example.order_management_service.service;

import com.example.order_management_service.dto.DeliveryStatusRequestDto;
import com.example.order_management_service.dto.DeliveryStatusResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeliveryStatusCacheService {

    private static final Duration TTL = Duration.ofMinutes(10);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void put(String orderId, DeliveryStatusRequestDto request) {
        DeliveryStatusResponseDto response = new DeliveryStatusResponseDto(
                orderId,
                request.getStatus(),
                request.getEtaMinutes(),
                request.getCourierId(),
                Instant.now()
        );
        redisTemplate.opsForValue().set(key(orderId), toJson(response), TTL);
    }

    public Optional<DeliveryStatusResponseDto> get(String orderId) {
        String json = redisTemplate.opsForValue().get(key(orderId));
        if (json == null) {
            return Optional.empty();
        }
        return Optional.of(fromJson(json, DeliveryStatusResponseDto.class));
    }

    public void delete(String orderId) {
        redisTemplate.delete(key(orderId));
    }

    private String key(String orderId) {
        return "delivery:status:" + orderId;
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize cache value", e);
        }
    }

    private <T> T fromJson(String value, Class<T> type) {
        try {
            return objectMapper.readValue(value, type);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize cache value", e);
        }
    }
}

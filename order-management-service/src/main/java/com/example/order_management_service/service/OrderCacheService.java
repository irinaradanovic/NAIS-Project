package com.example.order_management_service.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class OrderCacheService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private final StringRedisTemplate stringRedisTemplate;

    public OrderCacheService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void putOrderStatus(String orderId, String status, Integer ttlMinutes) {
        String key = statusKey(orderId);
        stringRedisTemplate.opsForValue().set(key, status);
        if (ttlMinutes != null && ttlMinutes > 0) {
            stringRedisTemplate.expire(key, Duration.ofMinutes(ttlMinutes));
        }
    }

    public String getOrderStatus(String orderId) {
        return stringRedisTemplate.opsForValue().get(statusKey(orderId));
    }

    public boolean deleteOrderStatus(String orderId) {
        Boolean deleted = stringRedisTemplate.delete(statusKey(orderId));
        return Boolean.TRUE.equals(deleted);
    }

    public long incrementDailyCount(LocalDate date) {
        String key = dailyCountKey(date);
        Long value = stringRedisTemplate.opsForValue().increment(key);
        stringRedisTemplate.expire(key, Duration.ofDays(7));
        return value != null ? value : 0L;
    }

    public Long getDailyCount(LocalDate date) {
        String value = stringRedisTemplate.opsForValue().get(dailyCountKey(date));
        if (value == null) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String statusKey(String orderId) {
        return "order:status:" + orderId;
    }

    private String dailyCountKey(LocalDate date) {
        return "order:daily-count:" + DATE_FORMAT.format(date);
    }
}

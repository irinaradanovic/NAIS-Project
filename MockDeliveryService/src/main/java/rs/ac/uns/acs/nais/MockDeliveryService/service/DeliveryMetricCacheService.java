package rs.ac.uns.acs.nais.MockDeliveryService.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.MockDeliveryService.dto.MetricResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class DeliveryMetricCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String PREFIX = "metric:";

    public DeliveryMetricCacheService(RedisTemplate<String, Object> redisTemplate,
                                      ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    // GET FROM CACHE
    public Optional<MetricResponse> get(String id) {
        try {
            Object value = redisTemplate.opsForValue().get(PREFIX + id);

            if (value == null) return Optional.empty();

            MetricResponse response =
                    objectMapper.readValue(value.toString(), MetricResponse.class);

            return Optional.of(response);

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // PUT INTO CACHE
    public void put(MetricResponse response) {
        try {
            String json = objectMapper.writeValueAsString(response);

            redisTemplate.opsForValue().set(
                    PREFIX + response.getId(),
                    json,
                    10,
                    TimeUnit.MINUTES
            );

        } catch (Exception ignored) {}
    }

    // DELETE FROM CACHE
    public void evict(String id) {
        redisTemplate.delete(PREFIX + id);
    }
}
package rs.ac.uns.acs.nais.FinanceManagementService.repository.saga;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.FinanceManagementService.model.saga.SagaDostaveState;

import java.time.Duration;
import java.util.Optional;

@Repository
@Slf4j
public class SagaStateRedisRepository {

    private static final String KEY_PREFIX = "saga:dostava:";
    private static final Duration TTL = Duration.ofDays(7);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public SagaStateRedisRepository(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void save(SagaDostaveState state) {
        try {
            String json = objectMapper.writeValueAsString(state);
            redisTemplate.opsForValue().set(KEY_PREFIX + state.getSagaId(), json, TTL);
        } catch (Exception e) {
            log.error("Greska prilikom upisa saga stanja u Redis: {}", e.getMessage());
            throw new RuntimeException("Ne mogu da sacuvam stanje sage u Redis", e);
        }
    }

    public Optional<SagaDostaveState> findById(String sagaId) {
        String json = redisTemplate.opsForValue().get(KEY_PREFIX + sagaId);
        if (json == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(json, SagaDostaveState.class));
        } catch (Exception e) {
            log.error("Greska prilikom citanja saga stanja iz Redisa: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
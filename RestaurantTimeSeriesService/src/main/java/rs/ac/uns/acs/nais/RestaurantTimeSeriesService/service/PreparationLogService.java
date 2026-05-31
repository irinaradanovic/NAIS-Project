package rs.ac.uns.acs.nais.RestaurantTimeSeriesService.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.model.PreparationLog;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.repository.PreparationLogRepositoryImpl;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PreparationLogService {

    private final PreparationLogRepositoryImpl repository;

    public void save(PreparationLog log) {
        if (log.getTime() == null) {
            log.setTime(Instant.now()); // if time is not set, set it to current time
        }

        repository.save(log);
    }

    public void saveAll(List<PreparationLog> logs) {
        repository.saveAll(logs);
    }

    public boolean deleteByRestaurantId(String restaurantId) {
        return repository.deleteByRestaurantId(restaurantId);
    }
}
package rs.ac.uns.acs.nais.RestaurantTimeSeriesService.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.dto.LocationCategoryComparisonDTO;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.dto.RestaurantAvgPreparationDTO;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.model.PreparationLog;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.repository.PreparationLogRepositoryImpl;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PreparationLogService {

    private final PreparationLogRepositoryImpl repository;

    @CacheEvict(value = {"top3Restaurants", "compareLocations"}, allEntries = true)
    public void save(PreparationLog plog) {
        if (plog.getTime() == null) {
            plog.setTime(Instant.now()); // if time is not set, set it to current time
        }

        log.info("[CACHE EVICT] Clearing cache.");

        repository.save(plog);
    }

    public void saveAll(List<PreparationLog> logs) {
        repository.saveAll(logs);
    }

    @CacheEvict(value = {"top3Restaurants", "compareLocations"}, allEntries = true)
    public boolean deleteByRestaurantId(String restaurantId) {
        log.info("[CACHE EVICT] Clearing cache.");
        return repository.deleteByRestaurantId(restaurantId);
    }

    public List<PreparationLog> findAll() {
        return repository.findAll();
    }

    public List<PreparationLog> findAllByRestaurantId(String restaurantId) {
        return repository.findAllByRestaurantId(restaurantId);
    }

    public List<PreparationLog> findAllByMenuItemId(String menuItemId) {
        return repository.findAllByMenuItemId(menuItemId);
    }

    @Cacheable(value = "top3Restaurants", key = "'top3'")
    public List<RestaurantAvgPreparationDTO> findTop3FastestRestaurants() {
        log.info("[CACHE] Querying InfluxDB for top3FastestRestaurants");
        return repository.findTop3FastestRestaurants();
    }

    @Cacheable(value = "compareLocations", key = "#restaurantId1 + ':' + #restaurantId2")
    public List<LocationCategoryComparisonDTO> compareLocationsByCategory(
            String restaurantId1, String restaurantId2) {
        log.info("[CACHE] Querying InfluxDB for compareLocations: {} vs {}",
                restaurantId1, restaurantId2);
        return repository.compareLocationsByCategory(restaurantId1, restaurantId2);
    }

    @CacheEvict(value = "top3Restaurants", allEntries = true)
    public void evictTop3Cache() {
        log.info("[CACHE EVICT] Clearing top3Restaurants cache");
    }
}
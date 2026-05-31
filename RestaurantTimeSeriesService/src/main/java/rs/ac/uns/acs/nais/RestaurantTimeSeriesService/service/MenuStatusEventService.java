package rs.ac.uns.acs.nais.RestaurantTimeSeriesService.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.dto.MenuCategoryActivityDTO;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.model.MenuStatusEvent;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.repository.MenuStatusEventRepositoryImpl;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuStatusEventService {

    private final MenuStatusEventRepositoryImpl repository;

    // Whenever theres a new entry, delete cache because the response wont be the same
    @CacheEvict(value = "categoryActivity", allEntries = true)
    public void save(MenuStatusEvent event) {
        if (event.getTime() == null) {
            event.setTime(Instant.now()); // if time is not set, set it to current time
        }
        log.info("[CACHE EVICT] Clearing cache.");

        repository.save(event);
    }

    public void saveAll(List<MenuStatusEvent> events) {
        repository.saveAll(events);
    }

    @CacheEvict(value = "categoryActivity", allEntries = true)
    public boolean deleteByMenuId(String menuId) {
        log.info("[CACHE EVICT] Clearing cache.");
        return repository.deleteByMenuId(menuId);
    }

    public List<MenuStatusEvent> findAll() {
        return repository.findAll();
    }

    public List<MenuStatusEvent> findAllByMenuId(String menuId) {
        return repository.findAllByMenuId(menuId);
    }

    @Cacheable(value = "categoryActivity", key = "#minNumberOfVersions")
    public List<MenuCategoryActivityDTO> categoryActivity(int  minNumberOfVersions) {
        log.info("[CACHE] Querying InfluxDB for categoryActivity, minVersions={}",
                minNumberOfVersions);
        return repository.findFrequentCategoryChanges(minNumberOfVersions);
    }

    @CacheEvict(value = "categoryActivity", allEntries = true)
    public void evictCategoryActivityCache() {
        log.info("[CACHE EVICT] Clearing categoryActivity cache");
    }
}
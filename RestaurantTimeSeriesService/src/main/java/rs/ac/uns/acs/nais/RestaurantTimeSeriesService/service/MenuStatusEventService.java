package rs.ac.uns.acs.nais.RestaurantTimeSeriesService.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.dto.MenuCategoryActivityDTO;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.model.MenuStatusEvent;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.repository.MenuStatusEventRepositoryImpl;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuStatusEventService {

    private final MenuStatusEventRepositoryImpl repository;

    public void save(MenuStatusEvent event) {
        if (event.getTime() == null) {
            event.setTime(Instant.now()); // if time is not set, set it to current time
        }

        repository.save(event);
    }

    public void saveAll(List<MenuStatusEvent> events) {
        repository.saveAll(events);
    }

    public boolean deleteByMenuId(String menuId) {
        return repository.deleteByMenuId(menuId);
    }

    public List<MenuStatusEvent> findAll() {
        return repository.findAll();
    }

    public List<MenuStatusEvent> findAllByMenuId(String menuId) {
        return repository.findAllByMenuId(menuId);
    }

    public List<MenuCategoryActivityDTO> categoryActivity(int  minNumberOfVersions) {
        return repository.findFrequentCategoryChanges(minNumberOfVersions);
    }
}
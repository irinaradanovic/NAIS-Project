package rs.ac.uns.acs.nais.RestaurantTimeSeriesService.repository;

import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.model.PreparationLog;
import java.util.List;

public interface PreparationLogRepository {
    void save(PreparationLog log);
    void saveAll(List<PreparationLog> logs);
    boolean deleteByRestaurantId(String restaurantId);
    List<PreparationLog> findAll();
    List<PreparationLog> findAllByRestaurantId(String restaurantId);
    List<PreparationLog> findAllByMenuItemId(String menuItemId);
}
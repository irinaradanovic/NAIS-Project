package rs.ac.uns.acs.nais.RestaurantTimeSeriesService.repository;

import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.model.PreparationLog;
import java.util.List;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.dto.LocationCategoryComparisonDTO;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.dto.RestaurantAvgPreparationDTO;

public interface PreparationLogRepository {
    void save(PreparationLog log);
    void saveAll(List<PreparationLog> logs);
    boolean deleteByRestaurantId(String restaurantId);
    List<PreparationLog> findAll();
    List<PreparationLog> findAllByRestaurantId(String restaurantId);
    List<PreparationLog> findAllByMenuItemId(String menuItemId);
    List<RestaurantAvgPreparationDTO> findTop3FastestRestaurants();
    List<LocationCategoryComparisonDTO> compareLocationsByCategory(String restaurantId1, String restaurantId2);
}
package rs.ac.uns.acs.nais.RestaurantTimeSeriesService.repository;

import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.model.MenuStatusEvent;
import java.util.List;

public interface MenuStatusEventRepository {
    void save(MenuStatusEvent event);
    void saveAll(List<MenuStatusEvent> events);
    boolean deleteByMenuId(String menuId);
}
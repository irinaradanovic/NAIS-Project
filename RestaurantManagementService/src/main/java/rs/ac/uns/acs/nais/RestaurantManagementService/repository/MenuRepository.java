package rs.ac.uns.acs.nais.RestaurantManagementService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Menu;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.MenuType;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends Neo4jRepository<Menu, Long> {

    // sve verzije istog menija
    List<Menu> findByMenuId(Integer menuId);

    // najnovija verzija menija
    @Query("MATCH (m:Menu {menuId: $menuId}) " +
            "RETURN m ORDER BY m.version DESC LIMIT 1")
    Optional<Menu> findLatestVersionByMenuId(@Param("menuId") Integer menuId);

    List<Menu> findByType(MenuType type);
}

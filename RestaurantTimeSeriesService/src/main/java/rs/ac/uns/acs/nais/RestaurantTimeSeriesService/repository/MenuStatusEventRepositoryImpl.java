package rs.ac.uns.acs.nais.RestaurantTimeSeriesService.repository;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.exceptions.InfluxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.dto.MenuCategoryActivityDTO;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.model.MenuStatusEvent;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MenuStatusEventRepositoryImpl implements MenuStatusEventRepository {

    private final InfluxDBClient influxDBClient;

    @Value("${spring.influx.bucket}")
    private String influxBucket;

    @Value("${spring.influx.org}")
    private String influxOrg;

    @Override
    public void save(MenuStatusEvent event) {
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        writeApi.writeMeasurement(influxBucket, influxOrg, WritePrecision.NS, event);
    }

    @Override
    public void saveAll(List<MenuStatusEvent> events) {
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        writeApi.writeMeasurements(influxBucket, influxOrg, WritePrecision.NS, events);
    }

    @Override
    public boolean deleteByMenuId(String menuId) {
        try {
            var deleteApi = influxDBClient.getDeleteApi();
            OffsetDateTime start = OffsetDateTime.now().minus(90, ChronoUnit.DAYS);
            OffsetDateTime stop = OffsetDateTime.now();
            String predicate = "_measurement=\"menu_status_events\" AND menuId=\"" + menuId + "\"";
            deleteApi.delete(start, stop, predicate, influxBucket, influxOrg);
            return true;
        } catch (InfluxException e) {
            log.error("Delete failed for menuId {}: ", menuId, e);
            return false;
        }
    }

    @Override
    public List<MenuStatusEvent> findAll() {
        String fluxQuery = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: -30d) " +
                        "|> filter(fn: (r) => r[\"_measurement\"] == \"menu_status_events\") " +
                        "|> pivot(rowKey: [\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")" +
                        "|> sort(columns: [\"_time\"], desc: true)",
                influxBucket
        );
        return influxDBClient.getQueryApi().query(fluxQuery, influxOrg, MenuStatusEvent.class);
    }

    @Override
    public List<MenuStatusEvent> findAllByMenuId(String menuId) {
        String fluxQuery = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: -30d) " +
                        "|> filter(fn: (r) => r[\"_measurement\"] == \"menu_status_events\") " +
                        "|> filter(fn: (r) => r[\"menuId\"] == \"%s\") " +
                        "|> pivot(rowKey: [\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")" +
                        "|> group() " +
                        "|> sort(columns: [\"_time\"], desc: true)",
                influxBucket, menuId
        );
        return influxDBClient.getQueryApi().query(fluxQuery, influxOrg, MenuStatusEvent.class);
    }

    @Override
    public List<MenuCategoryActivityDTO> findFrequentCategoryChanges(int minVersionThreshold) {
        String flux = String.format("""
        from(bucket: "%s")
          |> range(start: -30d)
          |> filter(fn: (r) => r["_measurement"] == "menu_status_events")
          |> filter(fn: (r) => r["_field"] == "totalCategoriesCount")
          |> filter(fn: (r) => r["eventType"] == "CATEGORY_ADDED" or r["eventType"] == "CATEGORY_REMOVED")
          |> filter(fn: (r) => int(v: r["version"]) >= %d)
          |> group(columns: ["restaurantName", "menuId", "eventType"])
          |> count(column: "_value")
          |> pivot(rowKey: ["restaurantName", "menuId"],
                   columnKey: ["eventType"],
                   valueColumn: "_value")
          |> group()
          |> sort(columns: ["CATEGORY_ADDED"], desc: true)
        """, influxBucket, minVersionThreshold);

        List<MenuCategoryActivityDTO> result = new ArrayList<>();
        influxDBClient.getQueryApi().query(flux, influxOrg).forEach(table ->
                table.getRecords().forEach(record -> {
                    MenuCategoryActivityDTO dto = new MenuCategoryActivityDTO();
                    dto.setRestaurantName((String) record.getValueByKey("restaurantName"));
                    dto.setMenuId((String) record.getValueByKey("menuId"));
                    Object added = record.getValueByKey("CATEGORY_ADDED");
                    dto.setCategoriesAdded(added != null ? ((Long) added) : 0L);
                    Object removed = record.getValueByKey("CATEGORY_REMOVED");
                    dto.setCategoriesRemoved(removed != null ? ((Long) removed) : 0L);
                    result.add(dto);
                })
        );
        return result;
    }
}
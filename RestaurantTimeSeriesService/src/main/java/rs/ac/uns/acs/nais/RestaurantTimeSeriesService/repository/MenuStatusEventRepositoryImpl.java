package rs.ac.uns.acs.nais.RestaurantTimeSeriesService.repository;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.exceptions.InfluxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.model.MenuStatusEvent;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
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
                "from(bucket: \"%s\") |> range(start: -30d) |> filter(fn: (r) => r[\"_measurement\"] == \"menu_status_events\")",
                influxBucket
        );
        QueryApi queryApi = influxDBClient.getQueryApi();
        return queryApi.query(fluxQuery, influxOrg, MenuStatusEvent.class);
    }

    @Override
    public List<MenuStatusEvent> findAllByMenuId(String menuId) {
        String fluxQuery = String.format(
                "from(bucket: \"%s\") |> range(start: -30d) " +
                        "|> filter(fn: (r) => r[\"_measurement\"] == \"menu_status_events\") " +
                        "|> filter(fn: (r) => r[\"menuId\"] == \"%s\")",
                influxBucket, menuId
        );
        QueryApi queryApi = influxDBClient.getQueryApi();
        return queryApi.query(fluxQuery, influxOrg, MenuStatusEvent.class);
    }
}
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
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.dto.LocationCategoryComparisonDTO;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.dto.RestaurantAvgPreparationDTO;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.model.PreparationLog;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PreparationLogRepositoryImpl implements PreparationLogRepository {

    private final InfluxDBClient influxDBClient;

    @Value("${spring.influx.bucket}")
    private String influxBucket;

    @Value("${spring.influx.org}")
    private String influxOrg;

    @Override
    public void save(PreparationLog log) {
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        writeApi.writeMeasurement(influxBucket, influxOrg, WritePrecision.NS, log);
    }

    @Override
    public void saveAll(List<PreparationLog> logs) {
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        writeApi.writeMeasurements(influxBucket, influxOrg, WritePrecision.NS, logs);
    }

    @Override
    public boolean deleteByRestaurantId(String restaurantId) {
        try {
            var deleteApi = influxDBClient.getDeleteApi();
            OffsetDateTime start = OffsetDateTime.now().minus(90, ChronoUnit.DAYS);
            OffsetDateTime stop = OffsetDateTime.now();
            String predicate = "_measurement=\"order_preparation_logs\" AND restaurantId=\"" + restaurantId + "\"";
            deleteApi.delete(start, stop, predicate, influxBucket, influxOrg);
            return true;
        } catch (InfluxException e) {
            log.error("Delete failed for restaurantId {}: ", restaurantId, e);
            return false;
        }
    }

    @Override
    public List<PreparationLog> findAll() {
        String fluxQuery = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: -30d) " +
                        "|> filter(fn: (r) => r[\"_measurement\"] == \"order_preparation_logs\") " +
                        "|> pivot(rowKey: [\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")" +
                        "|> sort(columns: [\"_time\"], desc: true)",
                influxBucket
        );
        return influxDBClient.getQueryApi().query(fluxQuery, influxOrg, PreparationLog.class);
    }

    @Override
    public List<PreparationLog> findAllByRestaurantId(String restaurantId) {
        String fluxQuery = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: -30d) " +
                        "|> filter(fn: (r) => r[\"_measurement\"] == \"order_preparation_logs\") " +
                        "|> filter(fn: (r) => r[\"restaurantId\"] == \"%s\") " +
                        "|> pivot(rowKey: [\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")" +
                        "|> group() " +
                        "|> sort(columns: [\"_time\"], desc: true)",
                influxBucket, restaurantId
        );
        return influxDBClient.getQueryApi().query(fluxQuery, influxOrg, PreparationLog.class);
    }

    @Override
    public List<PreparationLog> findAllByMenuItemId(String menuItemId) {
        String fluxQuery = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: -30d) " +
                        "|> filter(fn: (r) => r[\"_measurement\"] == \"order_preparation_logs\") " +
                        "|> filter(fn: (r) => r[\"menuItemId\"] == \"%s\") " +  // ← ISPRAVLJENO
                        "|> pivot(rowKey: [\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")" +
                        "|> group() " +
                        "|> sort(columns: [\"_time\"], desc: true)",
                influxBucket, menuItemId
        );
        return influxDBClient.getQueryApi().query(fluxQuery, influxOrg, PreparationLog.class);
    }


    @Override
    public List<RestaurantAvgPreparationDTO> findTop3FastestRestaurants() {
        String flux = String.format("""
        from(bucket: "%s")
          |> range(start: -14d)
          |> filter(fn: (r) => r["_measurement"] == "order_preparation_logs")
          |> filter(fn: (r) => r["_field"] == "actualDurationMinutes")
          |> group(columns: ["restaurantId", "restaurantName"])
          |> filter(fn: (r) => r["_value"] > 0.0)
          |> mean(column: "_value")
          |> map(fn: (r) => ({r with avgDurationMinutes: r._value}))
          |> group()
          |> sort(columns: ["avgDurationMinutes"], desc: false)
          |> limit(n: 3)
        """, influxBucket);

        List<RestaurantAvgPreparationDTO> result = new ArrayList<>();
        influxDBClient.getQueryApi().query(flux, influxOrg).forEach(table ->
                table.getRecords().forEach(record -> {
                    RestaurantAvgPreparationDTO dto = new RestaurantAvgPreparationDTO();
                    dto.setRestaurantId((String) record.getValueByKey("restaurantId"));
                    dto.setRestaurantName((String) record.getValueByKey("restaurantName"));
                    dto.setAvgDurationMinutes((Double) record.getValueByKey("avgDurationMinutes"));
                    result.add(dto);
                })
        );
        return result;
    }

    @Override
    public List<LocationCategoryComparisonDTO> compareLocationsByCategory(
            String restaurantId1, String restaurantId2) {

        String flux = String.format("""
        from(bucket: "%s")
          |> range(start: -30d)
          |> filter(fn: (r) => r["_measurement"] == "order_preparation_logs")
          |> filter(fn: (r) => r["restaurantId"] == "%s" or r["restaurantId"] == "%s")
          |> filter(fn: (r) => r["_field"] == "actualDurationMinutes")
          |> group(columns: ["restaurantName", "categoryName"])
          |> mean(column: "_value")
          |> map(fn: (r) => ({r with avgDurationMinutes: r._value}))
          |> group()
          |> sort(columns: ["categoryName", "avgDurationMinutes"], desc: false)
        """, influxBucket, restaurantId1, restaurantId2);

        List<LocationCategoryComparisonDTO> result = new ArrayList<>();
        influxDBClient.getQueryApi().query(flux, influxOrg).forEach(table ->
                table.getRecords().forEach(record -> {
                    LocationCategoryComparisonDTO dto = new LocationCategoryComparisonDTO();
                    dto.setRestaurantName((String) record.getValueByKey("restaurantName"));
                    dto.setCategoryName((String) record.getValueByKey("categoryName"));
                    dto.setAvgDurationMinutes((Double) record.getValueByKey("avgDurationMinutes"));
                    result.add(dto);
                })
        );
        return result;
    }
}
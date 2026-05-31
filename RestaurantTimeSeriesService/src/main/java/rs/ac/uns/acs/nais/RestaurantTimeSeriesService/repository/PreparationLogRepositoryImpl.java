package rs.ac.uns.acs.nais.RestaurantTimeSeriesService.repository;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.exceptions.InfluxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.model.PreparationLog;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
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
}
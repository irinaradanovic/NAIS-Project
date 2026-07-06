package rs.ac.uns.acs.nais.MockDeliveryService.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.MockDeliveryService.dto.CreateMetricRequest;
import rs.ac.uns.acs.nais.MockDeliveryService.dto.MetricResponse;
import rs.ac.uns.acs.nais.MockDeliveryService.util.MetricMapper;
import rs.ac.uns.acs.nais.MockDeliveryService.model.DeliveryAssignmentMetric;
import com.influxdb.client.DeleteApi;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class DeliveryMetricService {

    private final InfluxDBClient influxDBClient;

    @Value("${spring.influx.bucket}")
    private String bucket;

    @Value("${spring.influx.org}")
    private String org;

    private final DeliveryMetricCacheService cacheService;

    public DeliveryMetricService(InfluxDBClient influxDBClient, DeliveryMetricCacheService cacheService) {
        this.influxDBClient = influxDBClient;
        this.cacheService = cacheService;
    }

    // CREATE
    public MetricResponse create(CreateMetricRequest request) {

        DeliveryAssignmentMetric metric = MetricMapper.toModel(request);

        influxDBClient.getWriteApiBlocking()
                .writeMeasurement(bucket, org, WritePrecision.NS, metric);

        MetricResponse response = MetricMapper.toResponse(metric);

        // (opciono) ne mora put u cache odmah, ali može:
        cacheService.put(response);

        return response;
    }

    // GET BY ID
    public Optional<MetricResponse> getById(String id) {

        // 1. PROVERA CACHE
        Optional<MetricResponse> cached = cacheService.get(id);
        if (cached.isPresent()) {
            return cached;
        }

        // 2. INFLUX QUERY (fallback)
        String flux = String.format("""
            from(bucket: "%s")
              |> range(start: -365d)
              |> filter(fn: (r) => r["_measurement"] == "delivery_assignments")
              |> filter(fn: (r) => r["id"] == "%s")
              |> last()
            """, bucket, id);

        List<FluxTable> tables = influxDBClient.getQueryApi().query(flux, org);

        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {

                MetricResponse response = mapRecord(record);

                // 3. UPIS U CACHE
                cacheService.put(response);

                return Optional.of(response);
            }
        }

        return Optional.empty();
    }

    // GET ALL (LIMITED)
    public List<MetricResponse> getAll(int limit) {

        String flux = String.format("""
                from(bucket: "%s")
                  |> range(start: -365d)
                  |> filter(fn: (r) => r["_measurement"] == "delivery_assignments")
                  |> sort(columns: ["_time"], desc: true)
                  |> limit(n: %d)
                """, bucket, limit);

        QueryApi queryApi = influxDBClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(flux, org);

        List<MetricResponse> result = new ArrayList<>();

        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                result.add(mapRecord(record));
            }
        }

        return result;
    }

    public void deleteById(String id) {
        Instant start = Instant.parse("1970-01-01T00:00:00Z");
        Instant stop = Instant.now().plusSeconds(60);

        String predicate = String.format(
                "_measurement=\"delivery_assignments\" AND id=\"%s\"",
                id
        );

        com.influxdb.client.domain.DeletePredicateRequest deletePredicateRequest =
                new com.influxdb.client.domain.DeletePredicateRequest();
        deletePredicateRequest.setStart(OffsetDateTime.from(start));
        deletePredicateRequest.setStop(OffsetDateTime.from(stop));
        deletePredicateRequest.setPredicate(predicate);

        // Ispravan poziv prima request, bucket i org
        influxDBClient.getDeleteApi().delete(deletePredicateRequest, bucket, org);

        cacheService.evict(id);
    }

    // helper: FluxRecord -> DTO
    private MetricResponse mapRecord(FluxRecord record) {

        return new MetricResponse(
                record.getValueByKey("id") != null ? record.getValueByKey("id").toString() : null,
                record.getValueByKey("narudzbinaId") != null ? record.getValueByKey("narudzbinaId").toString() : null,
                record.getValueByKey("adresa") != null ? record.getValueByKey("adresa").toString() : null,
                record.getValueByKey("zona") != null ? record.getValueByKey("zona").toString() : null,
                record.getValueByKey("dostavljacId") != null ? record.getValueByKey("dostavljacId").toString() : null,
                record.getValueByKey("status") != null ? record.getValueByKey("status").toString() : null,
                record.getValueByKey("deliveryMinutes") != null
                        ? Double.valueOf(record.getValueByKey("deliveryMinutes").toString())
                        : null,
                record.getTime()
        );
    }
}
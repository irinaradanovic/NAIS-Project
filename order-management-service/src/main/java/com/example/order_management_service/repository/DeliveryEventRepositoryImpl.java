package com.example.order_management_service.repository;

import com.example.order_management_service.dto.CityDistanceDto;
import com.example.order_management_service.dto.CourierDelayDto;
import com.example.order_management_service.dto.DeliveryAvgByDayDto;
import com.example.order_management_service.model.DeliveryEvent;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.exceptions.InfluxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DeliveryEventRepositoryImpl implements DeliveryEventRepository {

    private final InfluxDBClient influxDBClient;

    @Value("${spring.influx.bucket}")
    private String influxBucket;

    @Value("${spring.influx.org}")
    private String influxOrg;

    @Override
    public void save(DeliveryEvent event) {
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        writeApi.writeMeasurement(influxBucket, influxOrg, WritePrecision.NS, event);
    }

    @Override
    public void saveAll(List<DeliveryEvent> events) {
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        writeApi.writeMeasurements(influxBucket, influxOrg, WritePrecision.NS, events);
    }

    @Override
    public boolean deleteByOrderId(String orderId) {
        try {
            var deleteApi = influxDBClient.getDeleteApi();
            OffsetDateTime start = OffsetDateTime.now().minus(90, ChronoUnit.DAYS);
            OffsetDateTime stop = OffsetDateTime.now();
            String predicate = "_measurement=\"delivery_events\" AND orderId=\"" + orderId + "\"";
            deleteApi.delete(start, stop, predicate, influxBucket, influxOrg);
            return true;
        } catch (InfluxException e) {
            log.error("Delete failed for orderId {}: ", orderId, e);
            return false;
        }
    }

    @Override
    public List<DeliveryEvent> findAll() {
        String fluxQuery = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: -30d) " +
                        "|> filter(fn: (r) => r[\"_measurement\"] == \"delivery_events\") " +
                        "|> pivot(rowKey: [\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")",
                influxBucket
        );
        return influxDBClient.getQueryApi().query(fluxQuery, influxOrg, DeliveryEvent.class);
    }

    @Override
    public List<DeliveryEvent> findByOrderId(String orderId) {
        String fluxQuery = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: -30d) " +
                        "|> filter(fn: (r) => r[\"_measurement\"] == \"delivery_events\") " +
                        "|> filter(fn: (r) => r[\"orderId\"] == \"%s\") " +
                        "|> pivot(rowKey: [\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")",
                influxBucket, orderId
        );
        return influxDBClient.getQueryApi().query(fluxQuery, influxOrg, DeliveryEvent.class);
    }

    @Override
    public List<DeliveryEvent> findByCourierId(String courierId) {
        String fluxQuery = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: -30d) " +
                        "|> filter(fn: (r) => r[\"_measurement\"] == \"delivery_events\") " +
                        "|> filter(fn: (r) => r[\"courierId\"] == \"%s\") " +
                        "|> pivot(rowKey: [\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")",
                influxBucket, courierId
        );
        return influxDBClient.getQueryApi().query(fluxQuery, influxOrg, DeliveryEvent.class);
    }

    @Override
    public List<DeliveryAvgByDayDto> findAvgDeliveryByDay(int days) {
        String flux = String.format("""
        from(bucket: "%s")
          |> range(start: -%dd)
          |> filter(fn: (r) => r["_measurement"] == "delivery_events")
          |> filter(fn: (r) => r["_field"] == "deliveryMinutes")
          |> filter(fn: (r) => r["status"] == "DELIVERED")
          |> aggregateWindow(every: 1d, fn: mean, createEmpty: false)
          |> map(fn: (r) => ({r with avgDeliveryMinutes: r._value}))
          |> keep(columns: ["_time", "avgDeliveryMinutes"])
          |> sort(columns: ["_time"], desc: false)
        """, influxBucket, days);

        List<DeliveryAvgByDayDto> result = new ArrayList<>();
        influxDBClient.getQueryApi().query(flux, influxOrg).forEach(table ->
                table.getRecords().forEach(record -> {
                    DeliveryAvgByDayDto dto = new DeliveryAvgByDayDto();
                    dto.setDay(record.getTime());
                    dto.setAvgDeliveryMinutes((Double) record.getValueByKey("avgDeliveryMinutes"));
                    result.add(dto);
                })
        );
        return result;
    }

    @Override
    public List<CourierDelayDto> findTopDelayedCouriers(int days, int limit) {
        String flux = String.format("""
        from(bucket: "%s")
          |> range(start: -%dd)
          |> filter(fn: (r) => r["_measurement"] == "delivery_events")
          |> filter(fn: (r) => r["_field"] == "delayMinutes")
          |> filter(fn: (r) => r["status"] == "DELIVERED")
          |> group(columns: ["courierId"])
          |> mean(column: "_value")
          |> map(fn: (r) => ({r with avgDelayMinutes: r._value}))
          |> group()
          |> sort(columns: ["avgDelayMinutes"], desc: true)
          |> limit(n: %d)
        """, influxBucket, days, limit);

        List<CourierDelayDto> result = new ArrayList<>();
        influxDBClient.getQueryApi().query(flux, influxOrg).forEach(table ->
                table.getRecords().forEach(record -> {
                    CourierDelayDto dto = new CourierDelayDto();
                    dto.setCourierId((String) record.getValueByKey("courierId"));
                    dto.setAvgDelayMinutes((Double) record.getValueByKey("avgDelayMinutes"));
                    result.add(dto);
                })
        );
        return result;
    }

    @Override
    public List<CityDistanceDto> findAvgDistanceByCity(int days) {
        String flux = String.format("""
        from(bucket: "%s")
          |> range(start: -%dd)
          |> filter(fn: (r) => r["_measurement"] == "delivery_events")
          |> filter(fn: (r) => r["_field"] == "distanceKm")
          |> group(columns: ["city"])
          |> mean(column: "_value")
          |> map(fn: (r) => ({r with avgDistanceKm: r._value}))
          |> group()
          |> sort(columns: ["avgDistanceKm"], desc: true)
        """, influxBucket, days);

        List<CityDistanceDto> result = new ArrayList<>();
        influxDBClient.getQueryApi().query(flux, influxOrg).forEach(table ->
                table.getRecords().forEach(record -> {
                    CityDistanceDto dto = new CityDistanceDto();
                    dto.setCity((String) record.getValueByKey("city"));
                    dto.setAvgDistanceKm((Double) record.getValueByKey("avgDistanceKm"));
                    result.add(dto);
                })
        );
        return result;
    }
}

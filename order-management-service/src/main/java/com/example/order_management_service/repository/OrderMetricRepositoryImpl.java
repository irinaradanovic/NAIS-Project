package com.example.order_management_service.repository;

import com.example.order_management_service.dto.PaymentDeliveryDistanceDto;
import com.example.order_management_service.dto.StatusAvgOrderValueDto;
import com.example.order_management_service.dto.TopCityRevenueDto;
import com.example.order_management_service.model.OrderMetric;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
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
public class OrderMetricRepositoryImpl implements OrderMetricRepository {

    private final InfluxDBClient influxDBClient;

    @Value("${spring.influx.bucket}")
    private String influxBucket;

    @Value("${spring.influx.org}")
    private String influxOrg;

    @Override
    public void save(OrderMetric metric) {
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        writeApi.writeMeasurement(influxBucket, influxOrg, WritePrecision.NS, metric);
    }

    @Override
    public void saveAll(List<OrderMetric> metrics) {
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        writeApi.writeMeasurements(influxBucket, influxOrg, WritePrecision.NS, metrics);
    }

    @Override
    public boolean deleteByOrderId(String orderId) {
        try {
            var deleteApi = influxDBClient.getDeleteApi();
            OffsetDateTime start = OffsetDateTime.now().minus(90, ChronoUnit.DAYS);
            OffsetDateTime stop = OffsetDateTime.now();
            String predicate = "_measurement=\"order_metrics\" AND orderId=\"" + orderId + "\"";
            deleteApi.delete(start, stop, predicate, influxBucket, influxOrg);
            return true;
        } catch (InfluxException e) {
            log.error("Delete failed for orderId {}: ", orderId, e);
            return false;
        }
    }

    @Override
    public List<OrderMetric> findAll() {
        String fluxQuery = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: -30d) " +
                        "|> filter(fn: (r) => r[\"_measurement\"] == \"order_metrics\") " +
                        "|> pivot(rowKey: [\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")",
                influxBucket
        );
        return influxDBClient.getQueryApi().query(fluxQuery, influxOrg, OrderMetric.class);
    }

    @Override
    public List<TopCityRevenueDto> findTopCitiesByRevenue(int limit) {
        String flux = String.format("""
        from(bucket: "%s")
          |> range(start: -30d)
          |> filter(fn: (r) => r["_measurement"] == "order_metrics")
          |> filter(fn: (r) => r["_field"] == "totalAmount")
          |> filter(fn: (r) => r["orderType"] == "DELIVERY")
          |> filter(fn: (r) => r["status"] == "COMPLETED")
          |> group(columns: ["city"])
          |> sum(column: "_value")
          |> map(fn: (r) => ({r with totalRevenue: r._value}))
          |> group()
          |> sort(columns: ["totalRevenue"], desc: true)
          |> limit(n: %d)
        """, influxBucket, limit);

        List<TopCityRevenueDto> result = new ArrayList<>();
        QueryApi queryApi = influxDBClient.getQueryApi();
        queryApi.query(flux, influxOrg).forEach(table ->
                table.getRecords().forEach(record -> {
                    TopCityRevenueDto dto = new TopCityRevenueDto();
                    dto.setCity((String) record.getValueByKey("city"));
                    dto.setTotalRevenue(toDouble(record.getValueByKey("totalRevenue")));
                    result.add(dto);
                })
        );
        return result;
    }

    @Override
    public List<StatusAvgOrderValueDto> findAvgOrderValueByStatus() {
        String flux = String.format("""
        from(bucket: "%s")
          |> range(start: -30d)
          |> filter(fn: (r) => r["_measurement"] == "order_metrics")
          |> filter(fn: (r) => r["_field"] == "totalAmount")
          |> filter(fn: (r) => r["status"] == "CANCELED" or r["status"] == "COMPLETED")
          |> group(columns: ["status"])
          |> mean(column: "_value")
          |> map(fn: (r) => ({r with avgOrderValue: r._value}))
          |> group()
          |> sort(columns: ["avgOrderValue"], desc: true)
        """, influxBucket);

        List<StatusAvgOrderValueDto> result = new ArrayList<>();
        QueryApi queryApi = influxDBClient.getQueryApi();
        queryApi.query(flux, influxOrg).forEach(table ->
                table.getRecords().forEach(record -> {
                    StatusAvgOrderValueDto dto = new StatusAvgOrderValueDto();
                    dto.setStatus((String) record.getValueByKey("status"));
                    dto.setAvgOrderValue(toDouble(record.getValueByKey("avgOrderValue")));
                    result.add(dto);
                })
        );
        return result;
    }

    @Override
    public List<PaymentDeliveryDistanceDto> findAvgDeliveryDistanceByPayment() {
        String flux = String.format("""
        from(bucket: "%s")
          |> range(start: -30d)
          |> filter(fn: (r) => r["_measurement"] == "order_metrics")
          |> filter(fn: (r) => r["_field"] == "deliveryKm")
          |> filter(fn: (r) => r["orderType"] == "DELIVERY")
          |> filter(fn: (r) => r["status"] == "COMPLETED")
          |> group(columns: ["paymentMethod"])
          |> mean(column: "_value")
          |> map(fn: (r) => ({r with avgDeliveryKm: r._value}))
          |> group()
          |> sort(columns: ["avgDeliveryKm"], desc: true)
        """, influxBucket);

        List<PaymentDeliveryDistanceDto> result = new ArrayList<>();
        QueryApi queryApi = influxDBClient.getQueryApi();
        queryApi.query(flux, influxOrg).forEach(table ->
                table.getRecords().forEach(record -> {
                    PaymentDeliveryDistanceDto dto = new PaymentDeliveryDistanceDto();
                    dto.setPaymentMethod((String) record.getValueByKey("paymentMethod"));
                    dto.setAvgDeliveryKm(toDouble(record.getValueByKey("avgDeliveryKm")));
                    result.add(dto);
                })
        );
        return result;
    }

    private Double toDouble(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        return Double.valueOf(value.toString());
    }
}

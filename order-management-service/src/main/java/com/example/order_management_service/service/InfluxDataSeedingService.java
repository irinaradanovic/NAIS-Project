package com.example.order_management_service.service;

import com.example.order_management_service.model.OrderMetric;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InfluxDataSeedingService implements CommandLineRunner {

    private static final int METRICS_COUNT = 2000;
    private final InfluxDBClient influxDBClient;

    @Value("${spring.influx.bucket}")
    private String influxBucket;

    @Value("${spring.influx.org}")
    private String influxOrg;

    private final Random random = new Random();

    private static final String[] STATUSES = {
            "CREATED", "CONFIRMED", "PREPARING", "OUT_FOR_DELIVERY", "COMPLETED", "CANCELED"
    };

    private static final String[] ORDER_TYPES = {"DELIVERY", "PICKUP"};
    private static final String[] PAYMENT_METHODS = {"CASH", "CARD", "CARD+CASH"};
    private static final String[] CHANNELS = {"WEB", "MOBILE", "PHONE"};
    private static final String[] CITIES = {"Novi Sad", "Beograd", "Nis", "Subotica", "Kragujevac"};

    @Override
    public void run(String... args) {
        if (isDatabaseAlreadySeeded()) {
            log.info("InfluxDB already has order metrics. Skipping seeding.");
            return;
        }

        List<OrderMetric> metrics = generateOrderMetrics();

        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        try {
            writeApi.writeMeasurements(influxBucket, influxOrg, WritePrecision.NS, metrics);
            log.info("Seeded {} order metrics", metrics.size());
        } catch (Exception e) {
            log.error("Error while inserting into InfluxDB: ", e);
        }
    }

    private List<OrderMetric> generateOrderMetrics() {
        List<OrderMetric> metrics = new ArrayList<>();
        Instant now = Instant.now();

        for (int i = 0; i < METRICS_COUNT; i++) {
            String orderId = "order" +(1000 + i);
            String status = pick(STATUSES);
            String orderType = pick(ORDER_TYPES);
            String city = pick(CITIES);
            String paymentMethod = pick(PAYMENT_METHODS);

            int itemCount = 1 + random.nextInt(8);
            double totalAmount = 500 + random.nextInt(4500) + random.nextDouble() * 100;
            double discountAmount = random.nextBoolean() ? random.nextInt(400) : 0.0;
            double deliveryKm = orderType.equals("DELIVERY") ? round(1 + random.nextDouble() * 12, 1) : 0.0;

            Instant time = now
                    .minus(random.nextInt(30), ChronoUnit.DAYS)
                    .minus(random.nextInt(24), ChronoUnit.HOURS)
                    .minus(random.nextInt(60), ChronoUnit.MINUTES);

            metrics.add(new OrderMetric(
                    orderId,
                    status,
                    orderType,
                    city,
                    paymentMethod,
                    round(totalAmount, 2),
                    itemCount,
                    deliveryKm,
                    round(discountAmount, 2),
                    time
            ));
        }
        return metrics;
    }

    private boolean isDatabaseAlreadySeeded() {
        try {
            String q = String.format(
                    "from(bucket: \"%s\") |> range(start: -40d) " +
                            "|> filter(fn: (r) => r[\"_measurement\"] == \"order_metrics\") " +
                            "|> limit(n: 1)",
                    influxBucket
            );
            return !influxDBClient.getQueryApi().query(q, influxOrg).isEmpty();
        } catch (Exception e) {
            log.error("Error checking InfluxDB state: ", e);
            return false;
        }
    }

    private String pick(String[] values) {
        return values[random.nextInt(values.length)];
    }

    private double round(double value, int decimals) {
        double factor = Math.pow(10, decimals);
        return Math.round(value * factor) / factor;
    }
}

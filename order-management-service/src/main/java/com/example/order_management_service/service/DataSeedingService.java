package com.example.order_management_service.service;

import com.example.order_management_service.model.CourierMetric;
import com.example.order_management_service.model.DeliveryEvent;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class DataSeedingService implements CommandLineRunner {

    private static final int DELIVERY_EVENT_COUNT = 1000;
    private static final int COURIER_METRIC_COUNT = 1000;

    private static final String[] CITIES = {"Novi Sad", "Beograd", "Nis", "Subotica"};
    private static final String[] STATUSES = {"CREATED", "ASSIGNED", "PICKED_UP", "DELIVERED"};
    private static final String[] VEHICLES = {"BIKE", "SCOOTER", "CAR"};
    private static final String[] RESTAURANTS = {"res-1001", "res-2002", "res-3003", "res-4004"};

    private final InfluxDBClient influxDBClient;

    @Value("${spring.influx.bucket}")
    private String influxBucket;

    @Value("${spring.influx.org}")
    private String influxOrg;

    private final Random random = new Random();

    @Override
    public void run(String... args) {
        if (isDatabaseAlreadySeeded()) {
            log.info("InfluxDB already has data. Skipping seeding.");
            return;
        }

        Instant now = Instant.now();
        List<DeliveryEvent> events = generateDeliveryEvents(now);
        List<CourierMetric> metrics = generateCourierMetrics(now);

        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        try {
            writeApi.writeMeasurements(influxBucket, influxOrg, WritePrecision.NS, events);
            writeApi.writeMeasurements(influxBucket, influxOrg, WritePrecision.NS, metrics);
            log.info("Seeded {} delivery events and {} courier metrics.", events.size(), metrics.size());
        } catch (Exception e) {
            log.error("Error while inserting into InfluxDB: ", e);
        }
    }

    private List<DeliveryEvent> generateDeliveryEvents(Instant now) {
        List<DeliveryEvent> events = new ArrayList<>();
        for (int i = 0; i < DELIVERY_EVENT_COUNT; i++) {
            String orderId = "order-" + (1000 + random.nextInt(9000));
            String courierId = "courier-" + (1 + random.nextInt(50));
            String city = CITIES[random.nextInt(CITIES.length)];
            String status = random.nextDouble() < 0.7 ? "DELIVERED" : STATUSES[random.nextInt(STATUSES.length)];
            String restaurantId = RESTAURANTS[random.nextInt(RESTAURANTS.length)];

            double distanceKm = 0.5 + random.nextDouble() * 15.0;
            double etaMinutes = 5.0 + distanceKm * 4.0 + random.nextDouble() * 5.0;
            double deliveryMinutes = etaMinutes + (random.nextDouble() * 10.0 - 3.0);
            if (deliveryMinutes < 3.0) {
                deliveryMinutes = 3.0;
            }
            double delayMinutes = Math.max(0.0, deliveryMinutes - etaMinutes);

            Instant time = now
                    .minus(random.nextInt(30), ChronoUnit.DAYS)
                    .minus(random.nextInt(24), ChronoUnit.HOURS)
                    .minus(random.nextInt(60), ChronoUnit.MINUTES);

            events.add(new DeliveryEvent(
                    orderId,
                    courierId,
                    status,
                    city,
                    restaurantId,
                    round(etaMinutes),
                    round(distanceKm),
                    round(deliveryMinutes),
                    round(delayMinutes),
                    time
            ));
        }
        return events;
    }

    private List<CourierMetric> generateCourierMetrics(Instant now) {
        List<CourierMetric> metrics = new ArrayList<>();
        for (int i = 0; i < COURIER_METRIC_COUNT; i++) {
            String courierId = "courier-" + (1 + random.nextInt(50));
            String city = CITIES[random.nextInt(CITIES.length)];
            String vehicleType = VEHICLES[random.nextInt(VEHICLES.length)];

            double avgDeliveryMinutes = 18.0 + random.nextDouble() * 25.0;
            double avgSpeedKmh = 12.0 + random.nextDouble() * 18.0;
            double rating = 3.5 + random.nextDouble() * 1.5;
            int completedDeliveries = 1 + random.nextInt(30);

            Instant time = now
                    .minus(random.nextInt(30), ChronoUnit.DAYS)
                    .minus(random.nextInt(24), ChronoUnit.HOURS);

            metrics.add(new CourierMetric(
                    courierId,
                    city,
                    vehicleType,
                    round(avgDeliveryMinutes),
                    round(avgSpeedKmh),
                    round(rating),
                    completedDeliveries,
                    time
            ));
        }
        return metrics;
    }

    private boolean isDatabaseAlreadySeeded() {
        String q1 = String.format(
                "from(bucket: \"%s\") |> range(start: -90d) |> filter(fn: (r) => r[\"_measurement\"] == \"delivery_events\") |> limit(n: 1)",
                influxBucket
        );
        String q2 = String.format(
                "from(bucket: \"%s\") |> range(start: -90d) |> filter(fn: (r) => r[\"_measurement\"] == \"courier_metrics\") |> limit(n: 1)",
                influxBucket
        );

        try {
            boolean hasDelivery = !influxDBClient.getQueryApi().query(q1, influxOrg).isEmpty();
            boolean hasMetrics = !influxDBClient.getQueryApi().query(q2, influxOrg).isEmpty();
            return hasDelivery || hasMetrics;
        } catch (Exception e) {
            log.error("Error checking InfluxDB state: ", e);
            return false;
        }
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}

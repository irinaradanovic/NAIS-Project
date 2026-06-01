package com.example.order_management_service.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.Organization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class InfluxBucketInitializer implements CommandLineRunner {

    private final InfluxDBClient influxDBClient;

    @Value("${spring.influx.org}")
    private String influxOrg;

    @Value("${spring.influx.bucket}")
    private String influxBucket;

    @Override
    public void run(String... args) {
        try {
                Organization org = influxDBClient.getOrganizationsApi()
                    .findOrganizations()
                    .stream()
                    .filter(o -> influxOrg.equals(o.getName()))
                    .findFirst()
                    .orElse(null);
            if (org == null) {
                log.error("Influx org not found: {}", influxOrg);
                return;
            }

            var bucketsApi = influxDBClient.getBucketsApi();
            var existing = bucketsApi.findBucketByName(influxBucket);
            if (existing == null) {
                bucketsApi.createBucket(influxBucket, org);
                log.info("Created Influx bucket: {}", influxBucket);
            } else {
                log.info("Influx bucket already exists: {}", influxBucket);
            }
        } catch (Exception e) {
            log.error("Failed to initialize Influx bucket {}: ", influxBucket, e);
        }
    }
}

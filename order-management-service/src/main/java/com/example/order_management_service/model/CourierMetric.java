package com.example.order_management_service.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Measurement(name = "courier_metrics")
public class CourierMetric {
    @Column(tag = true)
    private String courierId;

    @Column(tag = true)
    private String city;

    @Column(tag = true)
    private String vehicleType;

    @Column
    private Double avgDeliveryMinutes;

    @Column
    private Double avgSpeedKmh;

    @Column
    private Double rating;

    @Column
    private Integer completedDeliveries;

    @Column(timestamp = true)
    private Instant time;
}

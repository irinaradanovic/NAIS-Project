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
@Measurement(name = "delivery_events")
public class DeliveryEvent {
    @Column(tag = true)
    private String orderId;

    @Column(tag = true)
    private String courierId;

    @Column(tag = true)
    private String status;

    @Column(tag = true)
    private String city;

    @Column(tag = true)
    private String restaurantId;

    @Column
    private Double etaMinutes;

    @Column
    private Double distanceKm;

    @Column
    private Double deliveryMinutes;

    @Column
    private Double delayMinutes;

    @Column(timestamp = true)
    private Instant time;
}

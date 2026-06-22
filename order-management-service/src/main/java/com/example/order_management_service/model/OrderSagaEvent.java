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
@Measurement(name = "order_saga_events")
public class OrderSagaEvent {

    @Column(tag = true)
    private String orderId;

    @Column(tag = true)
    private String restaurantId;

    @Column(tag = true)
    private String eventType;

    @Column(tag = true)
    private String status;

    @Column
    private String reason;

    @Column
    private Integer itemCount;

    @Column(timestamp = true)
    private Instant time;
}

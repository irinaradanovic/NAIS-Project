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
@Measurement(name = "order_status_events")
public class OrderStatusEvent {

    @Column(tag = true)
    private String orderId;

    @Column(tag = true)
    private String status;

    @Column(tag = true)
    private String channel;

    @Column(tag = true)
    private String paymentMethod;

    @Column
    private Double processingSeconds;

    @Column
    private Double totalAmount;

    @Column(timestamp = true)
    private Instant time;
}

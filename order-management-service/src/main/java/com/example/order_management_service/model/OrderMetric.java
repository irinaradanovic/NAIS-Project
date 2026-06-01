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
@Measurement(name = "order_metrics")
public class OrderMetric {

    @Column(tag = true)
    private String orderId;

    @Column(tag = true)
    private String status;

    @Column(tag = true)
    private String orderType;

    @Column(tag = true)
    private String city;

    @Column(tag = true)
    private String paymentMethod;

    @Column
    private Double totalAmount;

    @Column
    private Integer itemCount;

    @Column
    private Double deliveryKm;

    @Column
    private Double discountAmount;

    @Column(timestamp = true)
    private Instant time;
}

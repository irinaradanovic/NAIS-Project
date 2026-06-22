package com.example.order_management_service.service;

import com.example.order_management_service.dto.OrderReportRowDto;
import com.example.order_management_service.dto.PaymentDeliveryDistanceDto;
import com.example.order_management_service.dto.StatusAvgOrderValueDto;
import com.example.order_management_service.dto.TopCityRevenueDto;
import com.example.order_management_service.model.OrderMetric;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.neo4j.core.Neo4jClient;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderReportServiceTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Neo4jClient neo4jClient;

    @Mock
    private OrderMetricService orderMetricService;

    @InjectMocks
    private OrderReportService orderReportService;

    @Test
    void generateReturnsNonEmptyPdf() {
        when(neo4jClient.query(anyString())
                .bindAll(anyMap())
                .fetchAs(eq(OrderReportRowDto.class))
                .mappedBy(any())
                .all())
                .thenReturn(List.of(new OrderReportRowDto(
                        "order-1",
                        LocalDateTime.of(2026, 6, 22, 15, 30),
                        "COMPLETED",
                        "DELIVERY",
                        "Bulevar Oslobodjenja 10, Novi Sad",
                        "res-petrus-7777",
                        2L
                )));
        when(orderMetricService.getAll()).thenReturn(List.of(new OrderMetric(
                "order-1",
                "COMPLETED",
                "DELIVERY",
                "Novi Sad",
                "CARD",
                1200.0,
                2,
                4.5,
                0.0,
                Instant.now()
        )));
        when(orderMetricService.getTopCitiesByRevenue(5)).thenReturn(List.of(new TopCityRevenueDto("Novi Sad", 1200.0)));
        when(orderMetricService.getAvgOrderValueByStatus()).thenReturn(List.of(new StatusAvgOrderValueDto("COMPLETED", 1200.0)));
        when(orderMetricService.getAvgDeliveryDistanceByPayment()).thenReturn(List.of(new PaymentDeliveryDistanceDto("CARD", 4.5)));

        byte[] pdf = orderReportService.generate(null, null, null, null);

        assertTrue(pdf.length > 1000);
        assertTrue(new String(pdf, 0, 4).startsWith("%PDF"));
    }
}

package rs.ac.uns.acs.nais.MockDeliveryService.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.MockDeliveryService.dto.influx.*;
import rs.ac.uns.acs.nais.MockDeliveryService.service.influx.DeliveryMetricsQueryService;

import java.util.List;

@RestController
@RequestMapping("/api/metrics/analytics")
@Tag(name = "Delivery Analytics", description = "Analitički Flux upiti nad InfluxDB bazom")
public class DeliveryAnalyticsController {

    private final DeliveryMetricsQueryService deliveryMetricsQueryService;

    public DeliveryAnalyticsController(DeliveryMetricsQueryService deliveryMetricsQueryService) {
        this.deliveryMetricsQueryService = deliveryMetricsQueryService;
    }

    @Operation(summary = "Prosečno vreme dostave po zoni")
    @GetMapping("/avg-delivery-time-by-zone")
    public List<AvgDeliveryTimeByZoneDTO> avgDeliveryTimeByZone() {
        return deliveryMetricsQueryService.avgDeliveryTimeByZone();
    }

    @Operation(summary = "Broj uspešnih dostava po dostavljaču")
    @GetMapping("/successful-deliveries-by-courier")
    public List<SuccessfulDeliveriesByCourierDTO> successfulDeliveriesByCourier() {
        return deliveryMetricsQueryService.successfulDeliveriesByCourier();
    }

    @Operation(summary = "Maksimalno vreme dostave po zoni")
    @GetMapping("/max-delivery-time-by-zone")
    public List<MaxDeliveryTimeByZoneDTO> maxDeliveryTimeByZone() {
        return deliveryMetricsQueryService.maxDeliveryTimeByZone();
    }

    @Operation(summary = "Trend prosečnog vremena dostave po zoni za poslednjih 7 dana")
    @GetMapping("/daily-delivery-trend")
    public List<DailyDeliveryTrendDTO> dailyDeliveryTrend() {
        return deliveryMetricsQueryService.dailyDeliveryTrend();
    }
}
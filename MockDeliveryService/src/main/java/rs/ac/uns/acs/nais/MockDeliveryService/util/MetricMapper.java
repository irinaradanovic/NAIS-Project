package rs.ac.uns.acs.nais.MockDeliveryService.util;

import rs.ac.uns.acs.nais.MockDeliveryService.dto.CreateMetricRequest;
import rs.ac.uns.acs.nais.MockDeliveryService.dto.MetricResponse;
import rs.ac.uns.acs.nais.MockDeliveryService.model.DeliveryAssignmentMetric;

import java.time.Instant;
import java.util.UUID;

public class MetricMapper {

    public static DeliveryAssignmentMetric toModel(CreateMetricRequest req) {
        return new DeliveryAssignmentMetric(
                UUID.randomUUID().toString(),
                req.getNarudzbinaId(),
                req.getAdresa(),
                req.getZona(),
                req.getDostavljacId(),
                req.getStatus(),
                req.getDeliveryMinutes(),
                Instant.now()
        );
    }

    public static MetricResponse toResponse(DeliveryAssignmentMetric m) {
        return new MetricResponse(
                m.getId(),
                m.getNarudzbinaId(),
                m.getAdresa(),
                m.getZona(),
                m.getDostavljacId(),
                m.getStatus(),
                m.getDeliveryMinutes(),
                m.getTime()
        );
    }
}
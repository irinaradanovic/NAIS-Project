package rs.ac.uns.acs.nais.MockDeliveryService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricResponse {

    private String id;

    private String narudzbinaId;

    private String adresa;

    private String zona;

    private String dostavljacId;

    private String status;

    private Double deliveryMinutes;

    private Instant time;
}
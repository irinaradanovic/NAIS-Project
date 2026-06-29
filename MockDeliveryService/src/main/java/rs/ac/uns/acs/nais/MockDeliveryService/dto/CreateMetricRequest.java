package rs.ac.uns.acs.nais.MockDeliveryService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMetricRequest {

    private String narudzbinaId;

    private String adresa;

    private String zona;

    private String dostavljacId;

    private String status;

    private Double deliveryMinutes;
}
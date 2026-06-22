package com.nais.delivery.DeliveryService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAssignmentRequest {

    private String sagaId;

    private String fondId;

    private String adresaDostave;

    private Double iznos;

    private String opis;

    private Instant vremeZahteva;

}
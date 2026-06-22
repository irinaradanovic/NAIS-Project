package com.nais.delivery.DeliveryService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAssignmentResponse {

    private String sagaId;

    private String status;

    private String dostavljacId;

    private String razlogNeuspeha;

}
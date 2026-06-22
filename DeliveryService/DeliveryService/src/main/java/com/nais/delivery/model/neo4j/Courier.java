package com.nais.delivery.DeliveryService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Node("Courier")
public class Courier {

    @Id
    private String id;

    private String ime;

    private String status;

    private Double latitude;

    private Double longitude;

    private Double prosecnaOcena;

}
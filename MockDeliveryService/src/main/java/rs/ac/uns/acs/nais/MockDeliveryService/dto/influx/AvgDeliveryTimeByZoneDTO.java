package rs.ac.uns.acs.nais.MockDeliveryService.dto.influx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvgDeliveryTimeByZoneDTO {

    private String zona;

    private Double averageMinutes;
}
package rs.ac.uns.acs.nais.MockDeliveryService.dto.influx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyDeliveryTrendDTO {

    private String zona;

    private Instant day;

    private Double averageDeliveryMinutes;

}
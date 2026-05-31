package rs.ac.uns.acs.nais.RestaurantTimeSeriesService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantAvgPreparationDTO implements Serializable {
    private String restaurantId;
    private String restaurantName;
    private Double avgDurationMinutes;
}
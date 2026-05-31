package rs.ac.uns.acs.nais.RestaurantTimeSeriesService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantAvgPreparationDTO {
    private String restaurantId;
    private String restaurantName;
    private Double avgDurationMinutes;
}
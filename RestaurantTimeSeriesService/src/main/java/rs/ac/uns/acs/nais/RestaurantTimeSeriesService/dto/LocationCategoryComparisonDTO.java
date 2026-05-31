package rs.ac.uns.acs.nais.RestaurantTimeSeriesService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationCategoryComparisonDTO {
    private String restaurantName;
    private String categoryName;
    private Double avgDurationMinutes;
}
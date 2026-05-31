package rs.ac.uns.acs.nais.RestaurantTimeSeriesService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationCategoryComparisonDTO implements Serializable {
    private String restaurantName;
    private String categoryName;
    private Double avgDurationMinutes;
}
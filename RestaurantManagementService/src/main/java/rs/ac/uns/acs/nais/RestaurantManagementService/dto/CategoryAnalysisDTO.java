package rs.ac.uns.acs.nais.RestaurantManagementService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class CategoryAnalysisDTO {
    private String restaurantName;
    private String categoryName;
    private Double avgCatPrice;
    private Double restaurantAvg;
}

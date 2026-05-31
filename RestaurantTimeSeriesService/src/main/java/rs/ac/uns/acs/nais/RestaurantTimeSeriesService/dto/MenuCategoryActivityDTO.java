package rs.ac.uns.acs.nais.RestaurantTimeSeriesService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuCategoryActivityDTO implements Serializable {
    private String menuId;
    private String restaurantName;
    private Long categoriesAdded;
    private Long categoriesRemoved;
}
package rs.ac.uns.acs.nais.RestaurantManagementService.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountAnalysisDTO {
    private String restaurantName;
    private String categoryName;
    private Long discountedCount;
    private Double avgDiscountedPrice;
}

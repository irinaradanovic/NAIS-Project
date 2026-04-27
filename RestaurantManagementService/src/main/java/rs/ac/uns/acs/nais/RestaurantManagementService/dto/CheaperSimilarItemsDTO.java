package rs.ac.uns.acs.nais.RestaurantManagementService.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheaperSimilarItemsDTO {
    private String itemName;
    private Double itemPrice;
    private String restaurantName;
}

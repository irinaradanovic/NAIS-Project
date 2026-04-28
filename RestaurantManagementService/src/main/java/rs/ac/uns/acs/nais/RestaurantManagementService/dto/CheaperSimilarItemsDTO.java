package rs.ac.uns.acs.nais.RestaurantManagementService.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CheaperSimilarItemsDTO {
    private String itemName;
    private Double itemPrice;
    private String restaurantName;
}

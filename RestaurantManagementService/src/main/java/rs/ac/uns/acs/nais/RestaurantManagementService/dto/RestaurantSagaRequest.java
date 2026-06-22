package rs.ac.uns.acs.nais.RestaurantManagementService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantSagaRequest {
    private UUID orderId;
    private UUID itemId;
    private Integer quantity;
}

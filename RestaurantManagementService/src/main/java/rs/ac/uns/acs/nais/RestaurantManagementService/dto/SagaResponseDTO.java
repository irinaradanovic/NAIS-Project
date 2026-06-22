package rs.ac.uns.acs.nais.RestaurantManagementService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SagaResponseDTO {
    private UUID orderId;
    private String status;
    private String reason;
}

package rs.ac.uns.acs.nais.RestaurantManagementService.dto;

import lombok.Data;

@Data
public class MenuItemDTO {
    private String name;
    private Double price;
    private Integer calories;
    private String description;
    private Integer quantity;
    private String unit;
    private Integer timeMin;
    private Integer timeMax;
}

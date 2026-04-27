package rs.ac.uns.acs.nais.RestaurantManagementService.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.MenuType;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuDTO {
    //private Integer menuId;       // null ako je potpuno novi meni, popunjeno ako je nova verzija
    private String name;
    private String description;
    private MenuType type;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;
    private LocalDate startSeasonDate;
    private LocalDate endSeasonDate;
}

package rs.ac.uns.acs.nais.RestaurantManagementService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Node
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Menu {

    @Id @GeneratedValue
    private Long id;

    private Integer menuId;  // ovo koristimo da bi pronasli sve verzije istog menija
    private String name;
    private Integer version;
    private String description;

    @Property("type")
    private MenuType type;

    // ako je meni vremenski (npr. dorucak)
    private LocalTime startTime;
    private LocalTime endTime;

    // ako je meni sezonski
    private LocalDate startSeasonDate;
    private LocalDate endSeasonDate;

    private LocalDate activationDate; // pocetak aktivnosti menija
    private LocalDate deactivationDate; // kada se verzija gasi, ako je null onda je verzija aktivna

    @Relationship(type = "HAS_CATEGORY", direction = Relationship.Direction.OUTGOING)
    private List<MenuCategoryRel> categories;  // svaki meni ima neke svoje kategorije

}

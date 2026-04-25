package rs.ac.uns.acs.nais.RestaurantManagementService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

import java.util.List;


@Node
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String address;
    private String contact;

    @Relationship(type = "HAS_MENU", direction = Relationship.Direction.OUTGOING)
    private List<HasMenuRel> menus;  // ide preko relacije sa properties

}

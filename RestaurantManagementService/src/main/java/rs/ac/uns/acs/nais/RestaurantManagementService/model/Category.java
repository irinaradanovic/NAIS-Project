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
public class Category {

    @Id @GeneratedValue
    private Long id;
    private String name;

    @Relationship(type = "INCLUDES_ITEM", direction = Relationship.Direction.OUTGOING)
    private List<IncludesItemRel> menuItems;
}

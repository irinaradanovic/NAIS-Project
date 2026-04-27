package rs.ac.uns.acs.nais.RestaurantManagementService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

@RelationshipProperties
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HasMenuRel {

    @Id
    @GeneratedValue
    private Long id;

    private Boolean active;

    @TargetNode
    private Menu menu;
}

package rs.ac.uns.acs.nais.RestaurantManagementService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

@RelationshipProperties
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuCategoryRel {
    @Id @GeneratedValue
    private Long id;

    private Integer order; // redni broj kategorije u meniju, zelimo da npr predjelo bude na pocetku, a pice na kraju

    @TargetNode
    private Category category;
}

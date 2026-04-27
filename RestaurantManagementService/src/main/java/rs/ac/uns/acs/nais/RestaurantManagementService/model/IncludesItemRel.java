package rs.ac.uns.acs.nais.RestaurantManagementService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;
import java.time.LocalDate;

@RelationshipProperties
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncludesItemRel {
    @Id @GeneratedValue
    private Long id;

    private Double discount;
    private LocalDate discountFrom;
    private LocalDate discountTo;

    @TargetNode
    private MenuItem menuItem;
}

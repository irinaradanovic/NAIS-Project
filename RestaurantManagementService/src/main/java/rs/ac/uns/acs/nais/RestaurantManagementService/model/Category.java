package rs.ac.uns.acs.nais.RestaurantManagementService.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Node
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id @GeneratedValue(GeneratedValue.UUIDGenerator.class)
    private UUID id;
    private String name;

    @Relationship(type = "INCLUDES_ITEM", direction = Relationship.Direction.OUTGOING)
    @JsonIgnore
    private List<IncludesItemRel> menuItems = new ArrayList<>();
}

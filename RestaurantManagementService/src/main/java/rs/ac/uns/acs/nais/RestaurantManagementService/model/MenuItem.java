package rs.ac.uns.acs.nais.RestaurantManagementService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.UUID;

@Node
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {
    @Id
    @GeneratedValue(GeneratedValue.UUIDGenerator.class)
    private UUID id;
    private String name;
    private Double price; // u RSD
    private Integer calories;
    private String description;
    private Integer quantity;
    private String unit;  // merna jedinica
    private Integer timeMin;  // minimalno vreme pripreme u minutama
    private Integer timeMax;
}

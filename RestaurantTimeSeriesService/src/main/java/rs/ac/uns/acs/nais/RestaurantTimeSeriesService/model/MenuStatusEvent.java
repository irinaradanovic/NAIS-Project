package rs.ac.uns.acs.nais.RestaurantTimeSeriesService.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Measurement(name = "menu_status_events")
public class MenuStatusEvent {

    @Column(tag = true)
    private String restaurantId;

    @Column(tag = true)
    private String restaurantName;

    @Column(tag = true)
    private String menuId;

    @Column(tag = true)
    private String version;

    @Column(tag = true)
    private String eventType;

    @Column(tag = true)
    private String affectedItemId; // if the update is related to a specific item, this is the id, could be null

    @Column
    private Double currentPrice;  // if the event is price updated, this is the new price

    @Column
    private Integer totalCategoriesCount;  // how many categories are on the menu after the update

    @Column(timestamp = true)
    private Instant time; // when the menu update happened
}

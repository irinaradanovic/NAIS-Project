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
@Measurement(name = "order_preparation_logs")
public class PreparationLog {
    @Column(tag = true)
    private String restaurantId;

    @Column(tag = true)
    private String restaurantName;

    @Column(tag = true)
    private String menuItemId;

    @Column(tag = true)
    private String categoryName;

    @Column
    private Double actualDurationMinutes;  // how long it took to prepare the item

    @Column(timestamp = true)
    private Instant time;
}

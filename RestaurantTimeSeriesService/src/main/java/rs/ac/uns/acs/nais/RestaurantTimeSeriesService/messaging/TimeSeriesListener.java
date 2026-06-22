package rs.ac.uns.acs.nais.RestaurantTimeSeriesService.messaging;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.model.PreparationLog;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.service.PreparationLogService;

import java.time.Instant;
import java.util.Map;

@Component
public class TimeSeriesListener {
    private final PreparationLogService preparationLogService;

    public TimeSeriesListener(PreparationLogService preparationLogService) {
        this.preparationLogService = preparationLogService;
    }

    @RabbitListener(queues = "restaurant.timeseries.queue")
    public void handleInfluxLogEvent(Map<String, Object> message) {
        System.out.println("Primljen asinhroni log za InfluxDB.");

        try {
            PreparationLog plog = new PreparationLog();
            plog.setRestaurantId((String) message.get("restaurantId"));
            plog.setRestaurantName((String) message.get("restaurantName"));
            plog.setMenuItemId((String) message.get("menuItemItemId"));
            plog.setCategoryName((String) message.get("categoryName"));
            plog.setActualDurationMinutes((Double) message.get("actualDurationMinutes"));
            plog.setTime(Instant.now());

            preparationLogService.save(plog);
            System.out.println("Log uspešno upisan u InfluxDB asinhrono!");

        } catch (Exception e) {
            System.err.println("Greška prilikom asinhronog upisa u Influx: " + e.getMessage());
        }
    }
}

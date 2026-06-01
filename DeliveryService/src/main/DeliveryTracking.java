package delivery.influxdb;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

@Measurement(name = "courier_status")
public class DeliveryTracking {

    @Column(tag = true)
    private String courierId;

    @Column(tag = true)
    private String city;

    @Column(tag = true)
    private String status;

    @Column
    private double latitude;

    @Column
    private double longitude;

    @Column
    private int activeOrders;

    @Column
    private int deliveryDuration;

    public DeliveryTracking() {
    }

    public DeliveryTracking(String courierId,
                            String city,
                            String status,
                            double latitude,
                            double longitude,
                            int activeOrders,
                            int deliveryDuration) {

        this.courierId = courierId;
        this.city = city;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.activeOrders = activeOrders;
        this.deliveryDuration = deliveryDuration;
    }

}
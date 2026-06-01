package delivery.influxdb;

import java.util.Random;

public class InfluxDataGenerator {

    public static void main(String[] args) {

        Random random = new Random();

        String[] cities = {
                "Belgrade",
                "NoviSad",
                "Nis",
                "Kragujevac"
        };

        String[] statuses = {
                "AVAILABLE",
                "DELIVERING",
                "OFFLINE"
        };

        for (int i = 0; i < 2000; i++) {

            String courierId = String.valueOf(random.nextInt(50) + 1);

            String city =
                    cities[random.nextInt(cities.length)];

            String status =
                    statuses[random.nextInt(statuses.length)];

            double latitude =
                    43 + random.nextDouble() * 3;

            double longitude =
                    19 + random.nextDouble() * 3;

            int activeOrders =
                    random.nextInt(6);

            int deliveryDuration =
                    random.nextInt(60);

            DeliveryTracking tracking =
                    new DeliveryTracking(
                            courierId,
                            city,
                            status,
                            latitude,
                            longitude,
                            activeOrders,
                            deliveryDuration
                    );

            System.out.println(tracking);
        }
    }
}
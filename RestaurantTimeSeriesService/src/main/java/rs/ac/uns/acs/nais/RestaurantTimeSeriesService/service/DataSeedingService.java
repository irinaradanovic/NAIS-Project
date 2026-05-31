package rs.ac.uns.acs.nais.RestaurantTimeSeriesService.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.model.MenuEventType;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.model.MenuStatusEvent;
import rs.ac.uns.acs.nais.RestaurantTimeSeriesService.model.PreparationLog;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataSeedingService implements CommandLineRunner{
    private final InfluxDBClient influxDBClient;

    @Value("${spring.influx.bucket}")
    private String influxBucket;

    @Value("${spring.influx.org}")
    private String influxOrg;

    private final Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        if (isDatabaseAlreadySeeded()) {
            log.info("InfluxDB already has data. Skipping seeding.");
            return;
        }
        log.info("Start seeding data...");

        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

        List<PreparationLog> logs = new ArrayList<>();
        List<MenuStatusEvent> events = new ArrayList<>();

        // restaurants from RestaurantManagementService
        String[][] restaurants = {
                {"res-gondola-ns-1111", "Gondola Novi Sad"},
                {"res-gondola-centar-2222", "Gondola Centar"},
                {"res-kfc-ns-3333", "KFC Novi Sad"},
                {"res-kfc-centar-4444", "KFC Centar"},
                {"res-loft-5555", "Loft Coffee & Food"},
                {"res-lanterna-6666", "Lanterna"},
                {"res-petrus-7777", "Petrus Caffe"}
        };

        // items from RestaurantManagementService
        // Format: { ID, NAME, CATEGORY, PRICE, MIN_TIME, MAX_TIME }
        Object[][] items = {
                // --- Gondola---
                {"item-margarita-g1", "Margarita", "Pizze", 850.0, 10, 15},
                {"item-carbonara-g1", "Carbonara", "Paste", 1100.0, 15, 20},
                {"item-tiramisu-g1", "Tiramisu", "Deserti", 400.0, 5, 5},
                {"item-margarita-g2", "Margarita", "Pizze", 950.0, 10, 15},
                {"item-quattro-formaggi", "Quattro Formaggi", "Pizze", 1150.0, 10, 15},
                {"item-carbonara-g2", "Carbonara", "Paste", 1200.0, 15, 20},
                {"item-penne-arrabiata", "Penne Arrabiata", "Paste", 1050.0, 12, 18},
                {"item-tiramisu-g2", "Tiramisu", "Deserti", 450.0, 5, 5},
                {"item-panna-cotta", "Panna Cotta", "Deserti", 380.0, 3, 5},
                {"item-cezar-salata", "Cezar Salata", "Salate", 850.0, 7, 10},
                {"item-jagnjetina", "Jagnjetina sa ruzmarinom", "Uskrsnja Jela", 2200.0, 40, 60},
                {"item-uskrsnja-pogaca", "Uskrsnja Pogaca", "Uskrsnja Jela", 350.0, 5, 8},
                {"item-cokoladni-kolac", "Cokoladni Kolac", "Uskrsnji Deserti", 480.0, 8, 12},

                // --- KFC ---
                {"item-kofice-3kom-v1", "Original Recipe Kofice 3kom", "Kofice", 750.0, 8, 12},
                {"item-zinger-v1", "Zinger Burger", "Burgeri", 650.0, 5, 8},
                {"item-kofice-3kom-v2", "Original Recipe Kofice 3kom", "Kofice", 780.0, 8, 12},
                {"item-zinger-v2", "Zinger Burger", "Burgeri", 680.0, 5, 8},
                {"item-twister-v2", "Twister Wrap", "Wrapperi", 720.0, 5, 8},
                {"item-kofice-3kom-v3", "Original Recipe Kofice 3kom", "Kofice", 820.0, 8, 12},
                {"item-hot-wings-6kom", "Hot Wings 6kom", "Kofice", 880.0, 10, 15},
                {"item-zinger-v3", "Zinger Burger", "Burgeri", 720.0, 5, 8},
                {"item-double-down", "Double Down", "Burgeri", 850.0, 8, 12},
                {"item-twister-v3", "Twister Wrap", "Wrapperi", 750.0, 5, 8},
                {"item-coleslaw", "Coleslaw", "Dodaci", 220.0, 1, 2},
                {"item-pomfrit", "Pomfrit", "Dodaci", 280.0, 3, 5},
                {"item-grander-burger", "Grander Burger", "Burgeri", 780.0, 8, 12},
                {"item-soft-vanila", "Soft Sladoled Vanila", "Sladoledi", 180.0, 1, 2},
                {"item-sundae-cokolada", "Sundae Cokolada", "Sladoledi", 250.0, 2, 3},
                {"item-milkshake-jagoda", "Milkshake Jagoda", "Sladoledi", 320.0, 3, 5},
                {"item-egg-burger", "Egg Burger", "Egg", 420.0, 5, 8},
                {"item-hash-brown", "Hash Brown", "Egg", 180.0, 3, 5},
                {"item-dorucak-box", "Dorucak Box", "Egg", 680.0, 7, 10},

                // --- Loft ---
                {"item-cappuccino", "Cappuccino", "Kafa i Piće", 280.0, 3, 5},
                {"item-avocado-toast", "Avocado Toast", "Sendviči", 720.0, 10, 12},

                // --- Lanterna ---
                {"item-lasagne-emiliane", "Lasagne Emiliane", "Specijaliteti kuće", 1350.0, 20, 30},
                {"item-saltimbocca", "Saltimbocca", "Specijaliteti kuće", 1600.0, 15, 25},
                {"item-osso-buco", "Osso Buco", "Specijaliteti kuće", 1850.0, 30, 50},

                // --- Petrus ---
                {"item-ribeye-stejk", "Ribeye Stejk", "Premium Stejkovi", 3500.0, 25, 40},
                {"item-tbone-stejk", "T-Bone Stejk", "Premium Stejkovi", 4200.0, 30, 45},
                {"item-omlet-sa-sirom", "Omlet sa sirom", "Jeftini Doručak", 450.0, 5, 10},
                {"item-przenice", "Prženice", "Jeftini Doručak", 380.0, 5, 10}
        };

        Instant now = Instant.now();

        for (int i = 0; i < 1000; i++) {
            String[] res = restaurants[random.nextInt(restaurants.length)];
            Object[] item = items[random.nextInt(items.length)];

            String resId = res[0];
            String resName = res[1];
            String itemId = (String) item[0];
            String catName = (String) item[2];
            int minTime = (int) item[4];
            int maxTime = (int) item[5];

            // generating a duration for item preparation
            double actualDuration = minTime + (random.nextDouble() * (maxTime - minTime + 4)) - 1;
            if (actualDuration < 1) actualDuration = 1.0;


            Instant timeStamp = now.minus(random.nextInt(30), ChronoUnit.DAYS)
                    .minus(random.nextInt(24), ChronoUnit.HOURS)
                    .minus(random.nextInt(60), ChronoUnit.MINUTES);

            PreparationLog logRecord = new PreparationLog(
                    resId, resName, itemId, catName,
                    Math.round(actualDuration * 10.0) / 10.0,
                    timeStamp
            );
            logs.add(logRecord);
        }

        String[] menuIds = {"101", "202", "303", "404", "505", "606", "707", "808"};
        MenuEventType[] eventTypes = MenuEventType.values();

        for (int i = 0; i < 1000; i++) {
            String[] res = restaurants[random.nextInt(restaurants.length)];
            Object[] item = items[random.nextInt(items.length)];
            MenuEventType eventType = eventTypes[random.nextInt(eventTypes.length)];

            double newPrice = 0.0;
            String affectedItem = "NONE";

            if (eventType == MenuEventType.ITEM_PRICE_CHANGED) {
                affectedItem = (String) item[0];
                newPrice = ((double) item[3]) + (random.nextInt(3) * 40) - 40;
            }

            Instant timeStamp = now.minus(random.nextInt(30), ChronoUnit.DAYS)
                    .minus(random.nextInt(24), ChronoUnit.HOURS);

            MenuStatusEvent eventRecord = new MenuStatusEvent(
                    res[0], res[1], menuIds[random.nextInt(menuIds.length)],
                    String.valueOf(random.nextInt(3) + 1),
                    eventType.name(), affectedItem, newPrice,
                    random.nextInt(3) + 2, timeStamp
            );
            events.add(eventRecord);
        }

        // batch insert
        try {
            writeApi.writeMeasurements(influxBucket, influxOrg, WritePrecision.NS, logs);
            writeApi.writeMeasurements(influxBucket, influxOrg, WritePrecision.NS, events);
            log.info("Inserted {} preparation logs and {} menu events to InfluxDB", logs.size(), events.size());
        } catch (Exception e) {
            log.error("Error while inserting into InfluxDB: ", e);
        }
    }

    private boolean isDatabaseAlreadySeeded() {
        try {
            String fluxQuery = String.format(
                    "from(bucket: \"%s\") |> range(start: -40d) |> limit(n: 1)",
                    influxBucket
            );

            var queryApi = influxDBClient.getQueryApi();
            var results = queryApi.query(fluxQuery, influxOrg);

            return !results.isEmpty();
        } catch (Exception e) {
            log.error("Error while checking InfluxDB state: ", e);
            return false;
        }
    }
}

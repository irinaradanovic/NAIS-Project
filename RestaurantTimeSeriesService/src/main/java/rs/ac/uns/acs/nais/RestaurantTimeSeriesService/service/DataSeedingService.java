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
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataSeedingService implements CommandLineRunner {

    private final InfluxDBClient influxDBClient;

    @Value("${spring.influx.bucket}")
    private String influxBucket;

    @Value("${spring.influx.org}")
    private String influxOrg;

    private final Random random = new Random();

    private static class ItemData {
        final String id;
        final String name;
        final String category;
        final double basePrice;
        final int minTime;
        final int maxTime;

        ItemData(String id, String name, String category, double basePrice, int minTime, int maxTime) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.basePrice = basePrice;
            this.minTime = minTime;
            this.maxTime = maxTime;
        }
    }

    private static class MenuData {
        final String menuId;      // e.g. "101", "303"
        final String menuName;
        final List<String> categories = new ArrayList<>();
        final List<ItemData> items = new ArrayList<>();

        MenuData(String menuId, String menuName) {
            this.menuId = menuId;
            this.menuName = menuName;
        }
    }

    private static class RestaurantData {
        final String id;
        final String name;
        final List<MenuData> menus = new ArrayList<>();

        RestaurantData(String id, String name) {
            this.id = id;
            this.name = name;
        }

        List<ItemData> allItems() {
            List<ItemData> all = new ArrayList<>();
            for (MenuData m : menus) all.addAll(m.items);
            return all;
        }
    }

    private List<RestaurantData> buildRestaurantData() {
        List<RestaurantData> restaurants = new ArrayList<>();

        //  Gondola Novi Sad
        RestaurantData gondolaNS = new RestaurantData("res-gondola-ns-1111", "Gondola Novi Sad");

        // Meni 101 v1  Standardni Meni (deaktiviran, ali logovi postoje iz prošlosti)
        MenuData gm1 = new MenuData("101", "Standardni Meni v1");
        gm1.categories.addAll(Arrays.asList("Pizze", "Paste", "Deserti"));
        gm1.items.add(new ItemData("item-margarita-g1",  "Margarita",  "Pizze",   850.0, 10, 15));
        gm1.items.add(new ItemData("item-carbonara-g1",  "Carbonara",  "Paste",  1100.0, 15, 20));
        gm1.items.add(new ItemData("item-tiramisu-g1",   "Tiramisu",   "Deserti", 400.0,  5,  5));

        // Meni 101 v2  Standardni Meni Proleće (aktivan)
        MenuData gm2 = new MenuData("101", "Standardni Meni Proleće v2");
        gm2.categories.addAll(Arrays.asList("Pizze", "Paste", "Deserti", "Salate"));
        gm2.items.add(new ItemData("item-margarita-g2",    "Margarita",        "Pizze",   950.0, 10, 15));
        gm2.items.add(new ItemData("item-quattro-formaggi","Quattro Formaggi", "Pizze",  1150.0, 10, 15));
        gm2.items.add(new ItemData("item-carbonara-g2",    "Carbonara",        "Paste",  1200.0, 15, 20));
        gm2.items.add(new ItemData("item-penne-arrabiata", "Penne Arrabiata",  "Paste",  1050.0, 12, 18));
        gm2.items.add(new ItemData("item-tiramisu-g2",     "Tiramisu",         "Deserti", 450.0,  5,  5));
        gm2.items.add(new ItemData("item-panna-cotta",     "Panna Cotta",      "Deserti", 380.0,  3,  5));
        gm2.items.add(new ItemData("item-cezar-salata",    "Cezar Salata",     "Salate",  850.0,  7, 10));

        // Meni 202  Uskršnji Specijal (aktivan)
        MenuData gmU = new MenuData("202", "Uskršnji Specijal");
        gmU.categories.addAll(Arrays.asList("Uskrsnja Jela", "Uskrsnji Deserti"));
        gmU.items.add(new ItemData("item-jagnjetina",       "Jagnjetina sa ruzmarinom", "Uskrsnja Jela",    2200.0, 40, 60));
        gmU.items.add(new ItemData("item-uskrsnja-pogaca",  "Uskrsnja Pogaca",          "Uskrsnja Jela",     350.0,  5,  8));
        gmU.items.add(new ItemData("item-cokoladni-kolac",  "Cokoladni Kolac",          "Uskrsnji Deserti",  480.0,  8, 12));

        gondolaNS.menus.add(gm1);
        gondolaNS.menus.add(gm2);
        gondolaNS.menus.add(gmU);
        restaurants.add(gondolaNS);

        // Gondola Centar  deli iste menije sa Gondola NS
        RestaurantData gondolaCentar = new RestaurantData("res-gondola-centar-2222", "Gondola Centar");
        gondolaCentar.menus.add(gm1);
        gondolaCentar.menus.add(gm2);
        gondolaCentar.menus.add(gmU);
        restaurants.add(gondolaCentar);

        //  KFC Novi Sad
        RestaurantData kfcNS = new RestaurantData("res-kfc-ns-3333", "KFC Novi Sad");

        // Meni 303 v1 (deaktiviran)
        MenuData km1 = new MenuData("303", "Klasicni Meni v1");
        km1.categories.addAll(Arrays.asList("Kofice", "Burgeri"));
        km1.items.add(new ItemData("item-kofice-3kom-v1", "Original Recipe Kofice 3kom", "Kofice",  750.0,  8, 12));
        km1.items.add(new ItemData("item-zinger-v1",      "Zinger Burger",               "Burgeri", 650.0,  5,  8));

        // Meni 303 v2 (deaktiviran)
        MenuData km2 = new MenuData("303", "Klasicni Meni v2");
        km2.categories.addAll(Arrays.asList("Kofice", "Burgeri", "Wrapperi"));
        km2.items.add(new ItemData("item-kofice-3kom-v2", "Original Recipe Kofice 3kom", "Kofice",   780.0,  8, 12));
        km2.items.add(new ItemData("item-zinger-v2",      "Zinger Burger",               "Burgeri",  680.0,  5,  8));
        km2.items.add(new ItemData("item-twister-v2",     "Twister Wrap",                "Wrapperi", 720.0,  5,  8));

        // Meni 303 v3 — aktivan
        MenuData km3 = new MenuData("303", "Klasicni Meni v3");
        km3.categories.addAll(Arrays.asList("Kofice", "Burgeri", "Wrapperi", "Dodaci"));
        km3.items.add(new ItemData("item-kofice-3kom-v3", "Original Recipe Kofice 3kom", "Kofice",   820.0,  8, 12));
        km3.items.add(new ItemData("item-hot-wings-6kom", "Hot Wings 6kom",              "Kofice",   880.0, 10, 15));
        km3.items.add(new ItemData("item-zinger-v3",      "Zinger Burger",               "Burgeri",  720.0,  5,  8));
        km3.items.add(new ItemData("item-double-down",    "Double Down",                 "Burgeri",  850.0,  8, 12));
        km3.items.add(new ItemData("item-twister-v3",     "Twister Wrap",                "Wrapperi", 750.0,  5,  8));
        km3.items.add(new ItemData("item-coleslaw",       "Coleslaw",                    "Dodaci",   220.0,  1,  2));
        km3.items.add(new ItemData("item-pomfrit",        "Pomfrit",                     "Dodaci",   280.0,  3,  5));
        km3.items.add(new ItemData("item-grander-burger", "Grander Burger",              "Burgeri",  780.0,  8, 12));

        // Meni 404 — Sladoled (sezonski)
        MenuData km4 = new MenuData("404", "Sladoled Meni");
        km4.categories.add("Sladoledi");
        km4.items.add(new ItemData("item-soft-vanila",      "Soft Sladoled Vanila", "Sladoledi", 180.0, 1, 2));
        km4.items.add(new ItemData("item-sundae-cokolada",  "Sundae Cokolada",      "Sladoledi", 250.0, 2, 3));
        km4.items.add(new ItemData("item-milkshake-jagoda", "Milkshake Jagoda",     "Sladoledi", 320.0, 3, 5));

        // Meni 505 — Doručak (vremenski meni)
        MenuData km5 = new MenuData("505", "Dorucak Meni");
        km5.categories.add("Egg");
        km5.items.add(new ItemData("item-egg-burger",  "Egg Burger",   "Egg", 420.0, 5, 8));
        km5.items.add(new ItemData("item-hash-brown",  "Hash Brown",   "Egg", 180.0, 3, 5));
        km5.items.add(new ItemData("item-dorucak-box", "Dorucak Box",  "Egg", 680.0, 7, 10));

        kfcNS.menus.add(km1);
        kfcNS.menus.add(km2);
        kfcNS.menus.add(km3);
        kfcNS.menus.add(km4);
        kfcNS.menus.add(km5);
        restaurants.add(kfcNS);

        // KFC Centar deli iste KFC menije
        RestaurantData kfcCentar = new RestaurantData("res-kfc-centar-4444", "KFC Centar");
        kfcCentar.menus.add(km1);
        kfcCentar.menus.add(km2);
        kfcCentar.menus.add(km3);
        kfcCentar.menus.add(km4);
        kfcCentar.menus.add(km5);
        restaurants.add(kfcCentar);

        // Loft Coffee & Food
        RestaurantData loft = new RestaurantData("res-loft-5555", "Loft Coffee & Food");
        MenuData lm1 = new MenuData("606", "Brunch Ponuda");
        lm1.categories.addAll(Arrays.asList("Kafa i Pice", "Sendvici"));
        lm1.items.add(new ItemData("item-cappuccino",    "Cappuccino",    "Kafa i Pice", 280.0,  3,  5));
        lm1.items.add(new ItemData("item-avocado-toast", "Avocado Toast", "Sendvici",    720.0, 10, 12));
        loft.menus.add(lm1);
        restaurants.add(loft);

        // Lanterna
        RestaurantData lanterna = new RestaurantData("res-lanterna-6666", "Lanterna");
        MenuData lantm = new MenuData("707", "Glavni Meni");
        lantm.categories.add("Specijaliteti kuce");
        lantm.items.add(new ItemData("item-lasagne-emiliane", "Lasagne Emiliane", "Specijaliteti kuce", 1350.0, 20, 30));
        lantm.items.add(new ItemData("item-saltimbocca",      "Saltimbocca",      "Specijaliteti kuce", 1600.0, 15, 25));
        lantm.items.add(new ItemData("item-osso-buco",        "Osso Buco",        "Specijaliteti kuce", 1850.0, 30, 50));
        lanterna.menus.add(lantm);
        restaurants.add(lanterna);

        // Petrus Caffe
        RestaurantData petrus = new RestaurantData("res-petrus-7777", "Petrus Caffe");
        MenuData pm = new MenuData("808", "Glavni Meni");
        pm.categories.addAll(Arrays.asList("Premium Stejkovi", "Jeftini Dorucak"));
        pm.items.add(new ItemData("item-ribeye-stejk",   "Ribeye Stejk",   "Premium Stejkovi", 3500.0, 25, 40));
        pm.items.add(new ItemData("item-tbone-stejk",    "T-Bone Stejk",   "Premium Stejkovi", 4200.0, 30, 45));
        pm.items.add(new ItemData("item-omlet-sa-sirom", "Omlet sa sirom", "Jeftini Dorucak",   450.0,  5, 10));
        pm.items.add(new ItemData("item-przenice",       "Przenice",       "Jeftini Dorucak",   380.0,  5, 10));
        petrus.menus.add(pm);
        restaurants.add(petrus);

        return restaurants;
    }


    @Override
    public void run(String... args) throws Exception {
        if (isDatabaseAlreadySeeded()) {
            log.info("InfluxDB already has data. Skipping seeding.");
            return;
        }
        log.info("Starting InfluxDB seeding...");

        List<RestaurantData> restaurants = buildRestaurantData();
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        Instant now = Instant.now();

        List<PreparationLog> logs   = generatePreparationLogs(restaurants, now);
        List<MenuStatusEvent> events = generateMenuStatusEvents(restaurants, now);

        try {
            writeApi.writeMeasurements(influxBucket, influxOrg, WritePrecision.NS, logs);
            writeApi.writeMeasurements(influxBucket, influxOrg, WritePrecision.NS, events);
            log.info("Seeded {} preparation logs and {} menu events.", logs.size(), events.size());
        } catch (Exception e) {
            log.error("Error while inserting into InfluxDB: ", e);
        }
    }


    private List<PreparationLog> generatePreparationLogs(List<RestaurantData> restaurants, Instant now) {
        List<PreparationLog> logs = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            RestaurantData res  = restaurants.get(random.nextInt(restaurants.size()));
            List<ItemData> all  = res.allItems();
            ItemData item       = all.get(random.nextInt(all.size()));

            double duration = item.minTime
                    + (random.nextDouble() * (item.maxTime - item.minTime))
                    + (random.nextDouble() * 3 - 1);     // ±1–3 min variance
            if (duration < 1) duration = 1.0;
            duration = Math.round(duration * 10.0) / 10.0;

            // Spread over the last 30 days
            Instant ts = now
                    .minus(random.nextInt(30), ChronoUnit.DAYS)
                    .minus(random.nextInt(24), ChronoUnit.HOURS)
                    .minus(random.nextInt(60), ChronoUnit.MINUTES);

            logs.add(new PreparationLog(
                    res.id,
                    res.name,
                    item.id,
                    item.category,
                    duration,
                    ts
            ));
        }
        return logs;
    }

    private List<MenuStatusEvent> generateMenuStatusEvents(List<RestaurantData> restaurants, Instant now) {
        List<MenuStatusEvent> events = new ArrayList<>();

        // Track current version per (restaurantId + menuId) pair
        Map<String, Integer> versionTracker = new HashMap<>();
        // Track current category count per (restaurantId + menuId) pair
        Map<String, Integer> categoryTracker = new HashMap<>();

        for (RestaurantData res : restaurants) {
            for (MenuData menu : res.menus) {
                String key = res.id + ":" + menu.menuId;
                versionTracker.putIfAbsent(key, 1);
                categoryTracker.putIfAbsent(key, menu.categories.size());
            }
        }

        MenuEventType[] allTypes = MenuEventType.values();

        for (int i = 0; i < 1000; i++) {
            RestaurantData res = restaurants.get(random.nextInt(restaurants.size()));

            // Pick one of this restaurant's menus
            MenuData menu = res.menus.get(random.nextInt(res.menus.size()));
            String key    = res.id + ":" + menu.menuId;

            MenuEventType eventType = allTypes[random.nextInt(allTypes.length)];

            int currentVersion       = versionTracker.get(key);
            int currentCategoryCount = categoryTracker.get(key);

            String affectedItemId = null;
            Double currentPrice   = null;

            switch (eventType) {

                case INITIAL_CREATION:
                    currentVersion = 1;
                    versionTracker.put(key, 1);
                    break;

                case MAJOR_INFO_CHANGE:
                    currentVersion++;
                    versionTracker.put(key, currentVersion);
                    break;

                case CATEGORY_ADDED:
                    currentCategoryCount++;
                    categoryTracker.put(key, currentCategoryCount);
                    currentVersion++;
                    versionTracker.put(key, currentVersion);
                    break;

                case CATEGORY_REMOVED:
                    if (currentCategoryCount > 1) {
                        currentCategoryCount--;
                        categoryTracker.put(key, currentCategoryCount);
                    }
                    currentVersion++;
                    versionTracker.put(key, currentVersion);
                    break;

                case ITEM_PRICE_CHANGED:
                    if (!menu.items.isEmpty()) {
                        ItemData changedItem = menu.items.get(random.nextInt(menu.items.size()));
                        affectedItemId = changedItem.id;

                        double pct    = (5 + random.nextInt(11)) / 100.0;
                        double sign   = random.nextBoolean() ? 1 : -1;
                        double raw    = changedItem.basePrice * (1 + sign * pct);
                        currentPrice  = Math.round(raw / 10.0) * 10.0;
                        if (currentPrice < 50) currentPrice = 50.0;
                    }
                    currentVersion++;
                    versionTracker.put(key, currentVersion);
                    break;
            }

            Instant ts = now
                    .minus(random.nextInt(30), ChronoUnit.DAYS)
                    .minus(random.nextInt(24), ChronoUnit.HOURS);

            events.add(new MenuStatusEvent(
                    res.id,
                    res.name,
                    menu.menuId,
                    String.valueOf(currentVersion),
                    eventType.name(),
                    affectedItemId != null ? affectedItemId : "NONE",
                    currentPrice   != null ? currentPrice   : 0.0,
                    currentCategoryCount,
                    ts
            ));
        }
        return events;
    }

    private boolean isDatabaseAlreadySeeded() {
        try {
            String q = String.format(
                    "from(bucket: \"%s\") |> range(start: -40d) |> limit(n: 1)", influxBucket);
            return !influxDBClient.getQueryApi().query(q, influxOrg).isEmpty();
        } catch (Exception e) {
            log.error("Error checking InfluxDB state: ", e);
            return false;
        }
    }
}
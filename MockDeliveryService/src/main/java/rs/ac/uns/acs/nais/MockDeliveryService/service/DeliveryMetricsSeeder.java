package rs.ac.uns.acs.nais.MockDeliveryService.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxTable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rs.ac.uns.acs.nais.MockDeliveryService.model.DeliveryAssignmentMetric;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DeliveryMetricsSeeder implements CommandLineRunner {

    private final InfluxDBClient influxDBClient;

    @Value("${spring.influx.bucket}")
    private String bucket;

    @Value("${spring.influx.org}")
    private String org;

    private static final int BROJ_ZAPISA = 2000;

    public DeliveryMetricsSeeder(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }

    @Override
    public void run(String... args) {
        if (vecPostojePodaci()) {
            System.out.println("[InfluxDB-Delivery] Podaci u delivery_assignments vec postoje. Preskacemo seeding.");
            return;
        }

        System.out.println("[InfluxDB-Delivery] Pokrecemo seeding demo podataka za dostave...");
        seedDeliveryData();
        System.out.println("[InfluxDB-Delivery] Seeding zavrsen!");
    }

    private boolean vecPostojePodaci() {
        String flux = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: -365d) " +
                        "|> filter(fn: (r) => r[\"_measurement\"] == \"delivery_assignments\") " +
                        "|> limit(n: 1)",
                bucket
        );
        QueryApi queryApi = influxDBClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(flux, org);
        return tables.stream().anyMatch(t -> !t.getRecords().isEmpty());
    }

    private void seedDeliveryData() {
        Random random = new Random(7);

        // adresa, zona, narudzbinaId (NAR-003 i NAR-002 simuliraju "vece" narudzbine sa visim fondom)
        String[][] lokacije = {
                {"Bulevar Oslobodjenja 12", "Bulevar Oslobodjenja", "NAR-003"},
                {"Bulevar Oslobodjenja 47", "Bulevar Oslobodjenja", "NAR-003"},
                {"Jovana Subotica 5", "Jovana Subotica", "NAR-001"},
                {"Jovana Subotica 21", "Jovana Subotica", "NAR-002"},
                {"Futoska 88", "Futoska", "NAR-002"},
                {"Futoska 14", "Futoska", "NAR-004"},
                {"Stevana Musica 3", "Ostalo", "NAR-004"},
                {"Narodnog Fronta 9", "Ostalo", "NAR-001"}
        };

        String[] dostavljaci = {"DOST-001", "DOST-002", "DOST-003", "DOST-004"};

        List<DeliveryAssignmentMetric> metrike = new ArrayList<>();
        Instant sada = Instant.now();

        for (int i = 0; i < BROJ_ZAPISA; i++) {
            String[] lokacija = lokacije[random.nextInt(lokacije.length)];
            String adresa = lokacija[0];
            String zona = lokacija[1];
            String narudzbinaId = lokacija[2];

            double stanjeFonda = switch (narudzbinaId) {
                case "NAR-003" -> 1100000.0 + random.nextDouble() * 200000.0;
                case "NAR-002" -> 800000.0 + random.nextDouble() * 150000.0;
                case "NAR-001" -> 400000.0 + random.nextDouble() * 100000.0;
                default -> 120000.0 + random.nextDouble() * 80000.0;
            };

            double baza = switch (zona) {
                case "Bulevar Oslobodjenja" -> 18.0;
                case "Jovana Subotica" -> 22.0;
                case "Futoska" -> 27.0;
                default -> 32.0;
            };
            double osnovnoVreme = stanjeFonda > 700000.0 ? baza - 4.0 : baza + 3.0;
            double sumVremena = random.nextDouble() * 6.0 - 3.0;
            double deliveryMinutes = Math.max(5.0, osnovnoVreme + sumVremena);

            boolean uspesno = random.nextDouble() < 0.92;
            String status = uspesno ? "USPESNO" : "NEUSPESNO";
            String dostavljacId = uspesno ? dostavljaci[random.nextInt(dostavljaci.length)] : null;

            Instant vreme = sada
                    .minus(random.nextInt(90), ChronoUnit.DAYS)
                    .minus(random.nextInt(24), ChronoUnit.HOURS)
                    .minus(random.nextInt(60), ChronoUnit.MINUTES);

            metrike.add(new DeliveryAssignmentMetric(
                    "SEED-" + i,
                    narudzbinaId,
                    adresa,
                    zona,
                    dostavljacId,
                    status,
                    uspesno ? Math.round(deliveryMinutes * 10.0) / 10.0 : null,
                    vreme
            ));
        }

        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        writeApi.writeMeasurements(bucket, org, WritePrecision.NS, metrike);
        System.out.println("[InfluxDB-Delivery] Upisano " + metrike.size() + " demo zapisa dostave.");
    }
}
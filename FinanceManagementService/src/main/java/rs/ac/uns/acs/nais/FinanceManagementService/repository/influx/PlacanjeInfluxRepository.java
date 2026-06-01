package rs.ac.uns.acs.nais.FinanceManagementService.repository.influx;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.exceptions.InfluxException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.FinanceManagementService.model.influx.PlacanjeDogadjaj;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Repository
public class PlacanjeInfluxRepository {

    private final InfluxDBClient influxDBClient;

    @Value("${spring.influx.bucket}")
    private String bucket;

    @Value("${spring.influx.org}")
    private String org;

    public PlacanjeInfluxRepository(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }

    // CREATE
    public void save(PlacanjeDogadjaj dogadjaj) {
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        writeApi.writeMeasurement(bucket, org, WritePrecision.NS, dogadjaj);
    }

    public void saveAll(List<PlacanjeDogadjaj> dogadjaji) {
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        writeApi.writeMeasurements(bucket, org, WritePrecision.NS, dogadjaji);
    }

    // READ
    public List<PlacanjeDogadjaj> findAll() {
        String flux = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: -365d) " +
                        "|> filter(fn: (r) => r[\"_measurement\"] == \"finance_payments\")",
                bucket
        );
        return influxDBClient.getQueryApi().query(flux, org, PlacanjeDogadjaj.class);
    }

    public List<PlacanjeDogadjaj> findByStanarId(String stanarId) {
        String flux = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: -365d) " +
                        "|> filter(fn: (r) => r[\"_measurement\"] == \"finance_payments\") " +
                        "|> filter(fn: (r) => r[\"stanarId\"] == \"%s\")",
                bucket, stanarId
        );
        return influxDBClient.getQueryApi().query(flux, org, PlacanjeDogadjaj.class);
    }

    public List<PlacanjeDogadjaj> findByTipRacuna(String tipRacuna) {
        String flux = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: -365d) " +
                        "|> filter(fn: (r) => r[\"_measurement\"] == \"finance_payments\") " +
                        "|> filter(fn: (r) => r[\"tipRacuna\"] == \"%s\")",
                bucket, tipRacuna
        );
        return influxDBClient.getQueryApi().query(flux, org, PlacanjeDogadjaj.class);
    }

    // DELETE
    public boolean deleteByStanarId(String stanarId) {
        try {
            var deleteApi = influxDBClient.getDeleteApi();
            OffsetDateTime start = OffsetDateTime.now().minus(730, ChronoUnit.DAYS);
            OffsetDateTime stop = OffsetDateTime.now().plus(1, ChronoUnit.DAYS);
            String predicate = "_measurement=\"finance_payments\" AND stanarId=\"" + stanarId + "\"";
            deleteApi.delete(start, stop, predicate, bucket, org);
            return true;
        } catch (InfluxException e) {
            return false;
        }
    }

    // SLOZENI UPITI

    /* Provera upisa podataka
    from(bucket: "restaurant_bucket")
  |> range(start: -365d)
  |> filter(fn: (r) => r["_measurement"] == "finance_payments")
  |> limit(n: 10)
     */

    // Upit 1: Ukupan iznos placanja po tipu racuna u poslednjih N dana
    public List<Map<String, Object>> ukupnoPoTipuRacuna(int poslednjiDani) {
        String flux = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: -%dd) " +
                        "|> filter(fn: (r) => r[\"_measurement\"] == \"finance_payments\") " +
                        "|> filter(fn: (r) => r[\"_field\"] == \"iznos\") " +
                        "|> group(columns: [\"tipRacuna\"]) " +
                        "|> sum(column: \"_value\") " +
                        "|> map(fn: (r) => ({tipRacuna: r[\"tipRacuna\"], ukupanIznos: r[\"_value\"]})) " +
                        "|> sort(columns: [\"ukupanIznos\"], desc: true)",
                bucket, poslednjiDani
                /*
                from(bucket: "restaurant_bucket")
  |> range(start: -365d)
  |> filter(fn: (r) => r["_measurement"] == "finance_payments")
  |> filter(fn: (r) => r["_field"] == "iznos")
  |> group(columns: ["tipRacuna"])
  |> sum(column: "_value")
  |> map(fn: (r) => ({tipRacuna: r["tipRacuna"], ukupanIznos: r["_value"]}))
  |> sort(columns: ["ukupanIznos"], desc: true)
                 */
        );
        QueryApi queryApi = influxDBClient.getQueryApi();
        return queryApi.query(flux, org).stream()
                .flatMap(table -> table.getRecords().stream())
                .map(record -> Map.<String, Object>of(
                        "tipRacuna", record.getValueByKey("tipRacuna"),
                        "ukupanIznos", record.getValue()
                ))
                .toList();
    }

    // Upit 2: Prosecno kasnjenje po stanaru (samo kasnjenja > 0)
    public List<Map<String, Object>> prosecnoKasnjenjePoPlacanjeStanara() {
        String flux = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: -365d) " +
                        "|> filter(fn: (r) => r[\"_measurement\"] == \"finance_payments\") " +
                        "|> filter(fn: (r) => r[\"_field\"] == \"daniKasnjenja\") " +
                        "|> filter(fn: (r) => r[\"_value\"] > 0.0) " +
                        "|> group(columns: [\"stanarId\", \"stanarEmail\"]) " +
                        "|> mean(column: \"_value\") " +
                        "|> map(fn: (r) => ({stanarId: r[\"stanarId\"], stanarEmail: r[\"stanarEmail\"], prosecnoKasnjenje: r[\"_value\"]})) " +
                        "|> sort(columns: [\"prosecnoKasnjenje\"], desc: true)",
                bucket
                /*
                from(bucket: "restaurant_bucket")
  |> range(start: -365d)
  |> filter(fn: (r) => r["_measurement"] == "finance_payments")
  |> filter(fn: (r) => r["_field"] == "daniKasnjenja")
  |> filter(fn: (r) => r["_value"] > 0.0)
  |> group(columns: ["stanarId", "stanarEmail"])
  |> mean(column: "_value")
  |> map(fn: (r) => ({stanarId: r["stanarId"], stanarEmail: r["stanarEmail"], prosecnoKasnjenje: r["_value"]}))
  |> sort(columns: ["prosecnoKasnjenje"], desc: true)
                 */
        );
        QueryApi queryApi = influxDBClient.getQueryApi();
        return queryApi.query(flux, org).stream()
                .flatMap(table -> table.getRecords().stream())
                .map(record -> Map.<String, Object>of(
                        "stanarId", String.valueOf(record.getValueByKey("stanarId")),
                        "stanarEmail", String.valueOf(record.getValueByKey("stanarEmail")),
                        "prosecnoKasnjenjeDana", record.getValue()
                ))
                .toList();
    }

    // Upit 3: Ukupan mesecni prihod samo od placanja na vreme (aggregateWindow)
    public List<Map<String, Object>> mesecniPrihodiNaVreme() {
        String flux = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: -365d) " +
                        "|> filter(fn: (r) => r[\"_measurement\"] == \"finance_payments\") " +
                        "|> filter(fn: (r) => r[\"_field\"] == \"iznos\") " +
                        "|> aggregateWindow(every: 30d, fn: sum, createEmpty: false) " +
                        "|> map(fn: (r) => ({period: string(v: r[\"_time\"]), ukupanPrihod: r[\"_value\"]}))",
                bucket
                /*
                from(bucket: "restaurant_bucket")
  |> range(start: -365d)
  |> filter(fn: (r) => r["_measurement"] == "finance_payments")
  |> filter(fn: (r) => r["_field"] == "iznos")
  |> aggregateWindow(every: 30d, fn: sum, createEmpty: false)
  |> map(fn: (r) => ({period: string(v: r["_time"]), ukupanPrihod: r["_value"]}))
                 */
        );
        QueryApi queryApi = influxDBClient.getQueryApi();
        return queryApi.query(flux, org).stream()
                .flatMap(table -> table.getRecords().stream())
                .map(record -> Map.<String, Object>of(
                        "period", String.valueOf(record.getTime()),
                        "ukupanPrihodNaVreme", record.getValue()
                ))
                .toList();
    }
}
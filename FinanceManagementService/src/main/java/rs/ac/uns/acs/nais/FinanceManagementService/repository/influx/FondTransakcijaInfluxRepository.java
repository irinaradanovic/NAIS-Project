package rs.ac.uns.acs.nais.FinanceManagementService.repository.influx;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.exceptions.InfluxException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.FinanceManagementService.model.influx.FondTransakcija;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Repository
public class FondTransakcijaInfluxRepository {

    private final InfluxDBClient influxDBClient;

    @Value("${spring.influx.bucket}")
    private String bucket;

    @Value("${spring.influx.org}")
    private String org;

    public FondTransakcijaInfluxRepository(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }

    // CREATE
    public void save(FondTransakcija transakcija) {
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        writeApi.writeMeasurement(bucket, org, WritePrecision.NS, transakcija);
    }

    public void saveAll(List<FondTransakcija> transakcije) {
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        writeApi.writeMeasurements(bucket, org, WritePrecision.NS, transakcije);
    }

    // READ
    public List<FondTransakcija> findAll() {
        String flux = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: -365d) " +
                        "|> filter(fn: (r) => r[\"_measurement\"] == \"finance_fond_transakcije\")",
                bucket
        );
        return influxDBClient.getQueryApi().query(flux, org, FondTransakcija.class);
    }

    public List<FondTransakcija> findByFondId(String fondId) {
        String flux = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: -365d) " +
                        "|> filter(fn: (r) => r[\"_measurement\"] == \"finance_fond_transakcije\") " +
                        "|> filter(fn: (r) => r[\"fondId\"] == \"%s\")",
                bucket, fondId
        );
        return influxDBClient.getQueryApi().query(flux, org, FondTransakcija.class);
    }

    public List<FondTransakcija> findByTipTransakcije(String tipTransakcije) {
        String flux = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: -365d) " +
                        "|> filter(fn: (r) => r[\"_measurement\"] == \"finance_fond_transakcije\") " +
                        "|> filter(fn: (r) => r[\"tipTransakcije\"] == \"%s\")",
                bucket, tipTransakcije
        );
        return influxDBClient.getQueryApi().query(flux, org, FondTransakcija.class);
    }

    // DELETE
    public boolean deleteByFondId(String fondId) {
        try {
            var deleteApi = influxDBClient.getDeleteApi();
            OffsetDateTime start = OffsetDateTime.now().minus(730, ChronoUnit.DAYS);
            OffsetDateTime stop = OffsetDateTime.now().plus(1, ChronoUnit.DAYS);
            String predicate = "_measurement=\"finance_fond_transakcije\" AND fondId=\"" + fondId + "\"";
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
  |> filter(fn: (r) => r["_measurement"] == "finance_fond_transakcije")
  |> limit(n: 10)
     */

    // Upit 1: Bilans fonda (uplate - isplate) po svakom fondu
    public List<Map<String, Object>> bilansPOFondu() {
        String flux = String.format(
                "uplate = from(bucket: \"%s\") " +
                        "|> range(start: -365d) " +
                        "|> filter(fn: (r) => r[\"_measurement\"] == \"finance_fond_transakcije\") " +
                        "|> filter(fn: (r) => r[\"_field\"] == \"iznos\") " +
                        "|> filter(fn: (r) => r[\"tipTransakcije\"] == \"UPLATA\") " +
                        "|> group(columns: [\"fondNaziv\"]) " +
                        "|> sum(column: \"_value\") " +
                        "|> rename(columns: {_value: \"ukupnoUplate\"})\n" +
                        "isplate = from(bucket: \"%s\") " +
                        "|> range(start: -365d) " +
                        "|> filter(fn: (r) => r[\"_measurement\"] == \"finance_fond_transakcije\") " +
                        "|> filter(fn: (r) => r[\"_field\"] == \"iznos\") " +
                        "|> filter(fn: (r) => r[\"tipTransakcije\"] == \"ISPLATA\") " +
                        "|> group(columns: [\"fondNaziv\"]) " +
                        "|> sum(column: \"_value\") " +
                        "|> rename(columns: {_value: \"ukupnoIsplate\"})\n" +
                        "join(tables: {u: uplate, i: isplate}, on: [\"fondNaziv\"]) " +
                        "|> map(fn: (r) => ({fondNaziv: r[\"fondNaziv\"], uplate: r[\"ukupnoUplate\"], isplate: r[\"ukupnoIsplate\"], bilans: r[\"ukupnoUplate\"] - r[\"ukupnoIsplate\"]})) " +
                        "|> sort(columns: [\"bilans\"], desc: true)",
                bucket, bucket
                /*
                uplate = from(bucket: "restaurant_bucket")
  |> range(start: -365d)
  |> filter(fn: (r) => r["_measurement"] == "finance_fond_transakcije")
  |> filter(fn: (r) => r["_field"] == "iznos")
  |> filter(fn: (r) => r["tipTransakcije"] == "UPLATA")
  |> group(columns: ["fondNaziv"])
  |> sum(column: "_value")
  |> rename(columns: {_value: "ukupnoUplate"})

isplate = from(bucket: "restaurant_bucket")
  |> range(start: -365d)
  |> filter(fn: (r) => r["_measurement"] == "finance_fond_transakcije")
  |> filter(fn: (r) => r["_field"] == "iznos")
  |> filter(fn: (r) => r["tipTransakcije"] == "ISPLATA")
  |> group(columns: ["fondNaziv"])
  |> sum(column: "_value")
  |> rename(columns: {_value: "ukupnoIsplate"})

join(tables: {u: uplate, i: isplate}, on: ["fondNaziv"])
  |> map(fn: (r) => ({fondNaziv: r["fondNaziv"], uplate: r["ukupnoUplate"], isplate: r["ukupnoIsplate"], bilans: r["ukupnoUplate"] - r["ukupnoIsplate"]}))
  |> sort(columns: ["bilans"], desc: true)
                 */
        );
        QueryApi queryApi = influxDBClient.getQueryApi();
        return queryApi.query(flux, org).stream()
                .flatMap(table -> table.getRecords().stream())
                .map(record -> Map.<String, Object>of(
                        "fondNaziv", String.valueOf(record.getValueByKey("fondNaziv")),
                        "bilans", record.getValueByKey("bilans"),
                        "uplate", record.getValueByKey("uplate"),
                        "isplate", record.getValueByKey("isplate")
                ))
                .toList();
    }

    // Upit 2: Mesecni ukupan obrt svih fondova (aggregateWindow)
    public List<Map<String, Object>> mesecniObrtFondova() {
        String flux = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: -365d) " +
                        "|> filter(fn: (r) => r[\"_measurement\"] == \"finance_fond_transakcije\") " +
                        "|> filter(fn: (r) => r[\"_field\"] == \"iznos\") " +
                        "|> aggregateWindow(every: 30d, fn: sum, createEmpty: false) " +
                        "|> map(fn: (r) => ({period: string(v: r[\"_time\"]), ukupanObrt: r[\"_value\"]}))",
                bucket
                /*
                from(bucket: "restaurant_bucket")
  |> range(start: -365d)
  |> filter(fn: (r) => r["_measurement"] == "finance_fond_transakcije")
  |> filter(fn: (r) => r["_field"] == "iznos")
  |> aggregateWindow(every: 30d, fn: sum, createEmpty: false)
  |> map(fn: (r) => ({period: string(v: r["_time"]), ukupanObrt: r["_value"]}))
                 */
        );
        QueryApi queryApi = influxDBClient.getQueryApi();
        return queryApi.query(flux, org).stream()
                .flatMap(table -> table.getRecords().stream())
                .map(record -> Map.<String, Object>of(
                        "period", String.valueOf(record.getTime()),
                        "ukupanObrt", record.getValue()
                ))
                .toList();
    }

    // Upit 3: Fondovi sa visokim isplatama u poslednjih godinu dana iznad praga
    public List<Map<String, Object>> fondoviSaVisokimIsplatama(double pragIsplate) {
        String flux = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: -365d) " +
                        "|> filter(fn: (r) => r[\"_measurement\"] == \"finance_fond_transakcije\") " +
                        "|> filter(fn: (r) => r[\"_field\"] == \"iznos\") " +
                        "|> filter(fn: (r) => r[\"tipTransakcije\"] == \"ISPLATA\") " +
                        "|> group(columns: [\"fondId\", \"fondNaziv\"]) " +
                        "|> sum(column: \"_value\") " +
                        "|> filter(fn: (r) => r[\"_value\"] > %f) " +
                        "|> map(fn: (r) => ({fondId: r[\"fondId\"], fondNaziv: r[\"fondNaziv\"], ukupnoIsplaceno: r[\"_value\"]})) " +
                        "|> sort(columns: [\"ukupnoIsplaceno\"], desc: true)",
                bucket, pragIsplate
                /*
                from(bucket: "restaurant_bucket")
  |> range(start: -365d)
  |> filter(fn: (r) => r["_measurement"] == "finance_fond_transakcije")
  |> filter(fn: (r) => r["_field"] == "iznos")
  |> filter(fn: (r) => r["tipTransakcije"] == "ISPLATA")
  |> group(columns: ["fondId", "fondNaziv"])
  |> sum(column: "_value")
  |> filter(fn: (r) => r["_value"] > 50000.0)
  |> map(fn: (r) => ({fondId: r["fondId"], fondNaziv: r["fondNaziv"], ukupnoIsplaceno: r["_value"]}))
  |> sort(columns: ["ukupnoIsplaceno"], desc: true)
                 */
        );
        QueryApi queryApi = influxDBClient.getQueryApi();
        return queryApi.query(flux, org).stream()
                .flatMap(table -> table.getRecords().stream())
                .map(record -> Map.<String, Object>of(
                        "fondId", String.valueOf(record.getValueByKey("fondId")),
                        "fondNaziv", String.valueOf(record.getValueByKey("fondNaziv")),
                        "ukupnoIsplaceno", record.getValue()
                ))
                .toList();
    }
}
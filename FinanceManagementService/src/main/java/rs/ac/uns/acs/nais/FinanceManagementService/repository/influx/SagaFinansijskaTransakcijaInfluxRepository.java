package rs.ac.uns.acs.nais.FinanceManagementService.repository.influx;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.FinanceManagementService.model.influx.SagaFinansijskaTransakcija;

import java.util.List;

@Repository
public class SagaFinansijskaTransakcijaInfluxRepository {

    private final InfluxDBClient influxDBClient;

    @Value("${spring.influx.bucket}")
    private String bucket;

    @Value("${spring.influx.org}")
    private String org;

    public SagaFinansijskaTransakcijaInfluxRepository(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }

    public void save(SagaFinansijskaTransakcija transakcija) {
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        writeApi.writeMeasurement(bucket, org, WritePrecision.NS, transakcija);
    }

    public List<SagaFinansijskaTransakcija> findBySagaId(String sagaId) {
        String flux = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: -365d) " +
                        "|> filter(fn: (r) => r[\"_measurement\"] == \"finance_saga_transakcije\") " +
                        "|> filter(fn: (r) => r[\"sagaId\"] == \"%s\")",
                bucket, sagaId
        );
        return influxDBClient.getQueryApi().query(flux, org, SagaFinansijskaTransakcija.class);
    }

    public List<SagaFinansijskaTransakcija> findAll() {
        String flux = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: -365d) " +
                        "|> filter(fn: (r) => r[\"_measurement\"] == \"finance_saga_transakcije\")",
                bucket
        );
        return influxDBClient.getQueryApi().query(flux, org, SagaFinansijskaTransakcija.class);
    }
}
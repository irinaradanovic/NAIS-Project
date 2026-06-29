package rs.ac.uns.acs.nais.MockDeliveryService.service.influx;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryMetricsQueryService {

    private final InfluxDBClient influxDBClient;

    @Value("${spring.influx.bucket}")
    private String bucket;

    @Value("${spring.influx.org}")
    private String org;

    public DeliveryMetricsQueryService(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }

    // helper
    private List<FluxTable> run(String flux) {
        QueryApi queryApi = influxDBClient.getQueryApi();
        return queryApi.query(flux, org);
    }

    //prosecno vreme dostave po zoni
    public List<FluxTable> avgDeliveryTimeByZone() {
        String flux = String.format("""
                    from(bucket: "%s")
                      |> range(start: -30d)
                      |> filter(fn: (r) => r["_measurement"] == "delivery_assignments")
                      |> filter(fn: (r) => exists r["zona"])
                      |> filter(fn: (r) => exists r["deliveryMinutes"])
                      |> group(columns: ["zona"])
                      |> mean(column: "deliveryMinutes")
                      |> sort(columns: ["_value"], desc: true)
                """, bucket);

        return run(flux);
    }

    // broj uspesnih dostava po dostavljacu
    public List<FluxTable> successfulDeliveriesByCourier() {
        String flux = String.format("""
                    from(bucket: "%s")
                      |> range(start: -30d)
                      |> filter(fn: (r) => r["_measurement"] == "delivery_assignments")
                      |> filter(fn: (r) => r["status"] == "USPESNO")
                      |> group(columns: ["dostavljacId"])
                      |> count(column: "status")
                      |> sort(columns: ["_value"], desc: true)
                """, bucket);

        return run(flux);
    }

    //max vreme dostave po zoni
    public List<FluxTable> maxDeliveryTimeByZone() {
        String flux = String.format("""
                    from(bucket: "%s")
                      |> range(start: -90d)
                      |> filter(fn: (r) => r["_measurement"] == "delivery_assignments")
                      |> filter(fn: (r) => exists r["deliveryMinutes"])
                      |> group(columns: ["zona"])
                      |> max(column: "deliveryMinutes")
                      |> sort(columns: ["_value"], desc: true)
                """, bucket);

        return run(flux);
    }

    //trend dostava po danima
    public List<FluxTable> dailyDeliveryTrend() {
        String flux = String.format("""
                    from(bucket: "%s")
                      |> range(start: -14d)
                      |> filter(fn: (r) => r["_measurement"] == "delivery_assignments")
                      |> filter(fn: (r) => r["status"] == "USPESNO")
                      |> aggregateWindow(every: 1d, fn: count, createEmpty: false)
                      |> sort(columns: ["_time"], desc: false)
                """, bucket);

        return run(flux);
    }
}
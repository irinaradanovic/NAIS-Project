package rs.ac.uns.acs.nais.MockDeliveryService.service.influx;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.MockDeliveryService.dto.influx.AvgDeliveryTimeByZoneDTO;
import rs.ac.uns.acs.nais.MockDeliveryService.dto.influx.DailyDeliveryTrendDTO;
import rs.ac.uns.acs.nais.MockDeliveryService.dto.influx.MaxDeliveryTimeByZoneDTO;
import rs.ac.uns.acs.nais.MockDeliveryService.dto.influx.SuccessfulDeliveriesByCourierDTO;

import java.util.ArrayList;
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
    public List<AvgDeliveryTimeByZoneDTO> avgDeliveryTimeByZone() {
        String flux = String.format("""
                from(bucket: "%s")
                  |> range(start: -30d)
                  |> filter(fn: (r) => r["_measurement"] == "delivery_assignments")
                  // Sklonjen "exists", dodat tacan filter za field:
                  |> filter(fn: (r) => r["_field"] == "deliveryMinutes")
                  |> group(columns: ["zona"])
                  |> mean() // mean automatski racuna prosek nad "_value" kolonom
                  |> sort(columns: ["_value"], desc: true)
            """, bucket);

        List<AvgDeliveryTimeByZoneDTO> result = new ArrayList<>();

        for (FluxTable table : run(flux)) {

            for (FluxRecord record : table.getRecords()) {

                result.add(
                        new AvgDeliveryTimeByZoneDTO(

                                record.getValueByKey("zona").toString(),

                                ((Number)record.getValue())
                                        .doubleValue()

                        )
                );
            }
        }

        return result;

    }

    // broj uspesnih dostava po dostavljacu
    public List<SuccessfulDeliveriesByCourierDTO> successfulDeliveriesByCourier() {
        String flux = String.format("""
                from(bucket: "%s")
                  |> range(start: -30d)
                  |> filter(fn: (r) => r["_measurement"] == "delivery_assignments")
                  |> filter(fn: (r) => r["_field"] == "deliveryMinutes")
                  |> filter(fn: (r) => r["status"] == "USPESNO")
                  |> group(columns: ["dostavljacId"])
                  |> count()
                  |> sort(columns: ["_value"], desc: true)
            """, bucket);

        List<SuccessfulDeliveriesByCourierDTO> result = new ArrayList<>();

        for (FluxTable table : run(flux)) {

            for (FluxRecord record : table.getRecords()) {

                result.add(

                        new SuccessfulDeliveriesByCourierDTO(

                                record.getValueByKey("dostavljacId").toString(),

                                ((Number)record.getValue()).longValue()

                        )
                );
            }
        }

        return result;
    }

    //max vreme dostave po zoni
    public List<MaxDeliveryTimeByZoneDTO> maxDeliveryTimeByZone() {
        String flux = String.format("""
                from(bucket: "%s")
                  |> range(start: -90d)
                  |> filter(fn: (r) => r["_measurement"] == "delivery_assignments")
                  // Ispravljen filter:
                  |> filter(fn: (r) => r["_field"] == "deliveryMinutes")
                  |> group(columns: ["zona"])
                  |> max() // max automatski uzima najvecu vrednost iz "_value"
                  |> sort(columns: ["_value"], desc: true)
            """, bucket);

        List<MaxDeliveryTimeByZoneDTO> result = new ArrayList<>();

        for (FluxTable table : run(flux)) {

            for (FluxRecord record : table.getRecords()) {

                result.add(

                        new MaxDeliveryTimeByZoneDTO(

                                record.getValueByKey("zona").toString(),

                                ((Number)record.getValue()).doubleValue()

                        )

                );
            }
        }

        return result;
    }

    //trend dostava po danima
    public List<DailyDeliveryTrendDTO> dailyDeliveryTrend() {
        String flux = String.format("""
                    from(bucket: "%s")
                      |> range(start: -30d) // Povecano na 30d radi boljeg trenda
                      |> filter(fn: (r) => r["_measurement"] == "delivery_assignments")
                      |> filter(fn: (r) => r["status"] == "USPESNO")
                      |> filter(fn: (r) => r["_field"] == "deliveryMinutes")
                      // Prvo agregiramo po danima unutar svake zone:
                      |> group(columns: ["zona"])
                      |> aggregateWindow(
                            every: 1d,
                            fn: mean,
                            createEmpty: false
                         )
                      |> sort(columns: ["_time"], desc: false) // Sortiramo hronoloski po vremenu za trend
                    """, bucket);

        List<DailyDeliveryTrendDTO> result = new ArrayList<>();

        for (FluxTable table : run(flux)) {

            for (FluxRecord record : table.getRecords()) {

                result.add(

                        new DailyDeliveryTrendDTO(

                                record.getValueByKey("zona").toString(),

                                record.getTime(),

                                ((Number)record.getValue()).doubleValue()

                        )

                );

            }

        }

        return result;
    }
}
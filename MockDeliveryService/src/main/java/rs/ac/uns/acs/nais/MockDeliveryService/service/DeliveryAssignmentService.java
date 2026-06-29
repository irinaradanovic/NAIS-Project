package rs.ac.uns.acs.nais.MockDeliveryService.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.MockDeliveryService.dto.SagaDeliveryRequestEventDTO;
import rs.ac.uns.acs.nais.MockDeliveryService.dto.SagaDeliveryResponseEventDTO;
import rs.ac.uns.acs.nais.MockDeliveryService.model.DeliveryAssignmentMetric;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeliveryAssignmentService {

    private final ObjectMapper objectMapper;
    private final Neo4jClient neo4jClient;
    private final InfluxDBClient influxDBClient;


    @Value("${spring.influx.bucket}")
    private String bucket;

    @Value("${spring.influx.org}")
    private String org;

    public DeliveryAssignmentService(
            ObjectMapper objectMapper,
            Neo4jClient neo4jClient,
            InfluxDBClient influxDBClient
            ) {

        this.objectMapper = objectMapper;
        this.neo4jClient = neo4jClient;
        this.influxDBClient = influxDBClient;

    }

    public void processDeliveryRequest(Message message) throws Exception {

        SagaDeliveryRequestEventDTO request =
                objectMapper.readValue(message.getBody(), SagaDeliveryRequestEventDTO.class);

        String zona = zonaZaAdresu(request.getAdresaDostave());

        Optional<Map<String, Object>> dostavljac = pronadjiDostavljaca(request);

        if (dostavljac.isEmpty()) {

            upisiMetriku(
                    request,
                    zona,
                    null,
                    "NEUSPESNO",
                    null
            );



            return;
        }

        String dostavljacId = String.valueOf(dostavljac.get().get("id"));

        upisiMetriku(
                request,
                zona,
                dostavljacId,
                "USPESNO",
                izracunajMinute(request.getFondId(), zona)
        );


    }

    private Optional<Map<String, Object>> pronadjiDostavljaca(SagaDeliveryRequestEventDTO request) {

        return neo4jClient.query("""
                MATCH (d:Dostavljac)
                WHERE d.status = 'SLOBODAN'
                  AND d.prosecnaOcena >= 4.5
                WITH d,
                     CASE
                        WHEN toLower($adresa) CONTAINS toLower(d.zona)
                        THEN 0
                        ELSE 1
                     END AS udaljenost
                ORDER BY udaljenost ASC,
                         d.prosecnaOcena DESC
                LIMIT 1
                SET d.status = 'ZAUZET',
                    d.trenutnaAdresa = $adresa,
                    d.poslednjaSagaId = $sagaId
                RETURN d.id AS id,
                       d.prosecnaOcena AS prosecnaOcena,
                       udaljenost AS udaljenost
                """)
                .bind(request.getAdresaDostave()).to("adresa")
                .bind(request.getSagaId()).to("sagaId")
                .fetch()
                .one();
    }

    private void upisiMetriku(
            SagaDeliveryRequestEventDTO request,
            String zona,
            String dostavljacId,
            String status,
            Double deliveryMinutes) {

        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

        writeApi.writeMeasurement(
                bucket,
                org,
                WritePrecision.NS,
                new DeliveryAssignmentMetric(
                        UUID.randomUUID().toString(),
                        request.getFondId(),
                        request.getAdresaDostave(),
                        zona,
                        dostavljacId,
                        status,
                        deliveryMinutes,
                        Instant.now()
                )
        );
    }

    private Optional<Double> stanjeFonda(String fondId) {

        return neo4jClient.query("""
                MATCH (f:FinansijskiFond {idOriginal: $fondId})
                RETURN f.ukupanIznos AS stanje
                """)
                .bind(fondId)
                .to("fondId")
                .fetchAs(Double.class)
                .mappedBy((typeSystem, record) ->
                        record.get("stanje").asDouble())
                .one();
    }

    private double izracunajMinute(String fondId, String zona) {

        double stanje = stanjeFonda(fondId).orElse(0.0);

        double baza = switch (zona) {
            case "Bulevar Oslobodjenja" -> 18.0;
            case "Jovana Subotica" -> 22.0;
            case "Futoska" -> 27.0;
            default -> 30.0;
        };

        return stanje > 700000.0
                ? baza - 4.0
                : baza + 3.0;
    }

    private String zonaZaAdresu(String adresa) {

        String lower = adresa == null ? "" : adresa.toLowerCase();

        if (lower.contains("bulevar")) {
            return "Bulevar Oslobodjenja";
        }

        if (lower.contains("subot")) {
            return "Jovana Subotica";
        }

        if (lower.contains("futos")) {
            return "Futoska";
        }

        return "Ostalo";
    }
}
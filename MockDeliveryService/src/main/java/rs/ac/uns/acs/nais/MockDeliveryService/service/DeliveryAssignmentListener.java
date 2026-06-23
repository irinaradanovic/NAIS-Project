package rs.ac.uns.acs.nais.MockDeliveryService.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;
import rs.ac.uns.acs.nais.MockDeliveryService.config.RabbitMQConfig;
import rs.ac.uns.acs.nais.MockDeliveryService.dto.SagaDeliveryRequestEventDTO;
import rs.ac.uns.acs.nais.MockDeliveryService.dto.SagaDeliveryResponseEventDTO;
import rs.ac.uns.acs.nais.MockDeliveryService.model.DeliveryAssignmentMetric;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Component
public class DeliveryAssignmentListener {

    private final ObjectMapper objectMapper;
    private final Neo4jClient neo4jClient;
    private final InfluxDBClient influxDBClient;
    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.influx.bucket}")
    private String bucket;

    @Value("${spring.influx.org}")
    private String org;

    public DeliveryAssignmentListener(ObjectMapper objectMapper, Neo4jClient neo4jClient,
                                      InfluxDBClient influxDBClient, RabbitTemplate rabbitTemplate) {
        this.objectMapper = objectMapper;
        this.neo4jClient = neo4jClient;
        this.influxDBClient = influxDBClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.DELIVERY_REQUEST_QUEUE)
    public void handleDeliveryRequest(Message message) throws Exception {
        SagaDeliveryRequestEventDTO request = objectMapper.readValue(message.getBody(), SagaDeliveryRequestEventDTO.class);
        String zona = zonaZaAdresu(request.getAdresaDostave());

        Optional<Map<String, Object>> dostavljac = neo4jClient.query("""
                MATCH (d:Dostavljac)
                WHERE d.status = 'SLOBODAN' AND d.prosecnaOcena >= 4.5
                WITH d,
                  CASE WHEN toLower($adresa) CONTAINS toLower(d.zona) THEN 0 ELSE 1 END AS udaljenost
                ORDER BY udaljenost ASC, d.prosecnaOcena DESC
                LIMIT 1
                SET d.status = 'ZAUZET',
                    d.trenutnaAdresa = $adresa,
                    d.poslednjaSagaId = $sagaId
                RETURN d.id AS id, d.prosecnaOcena AS prosecnaOcena, udaljenost AS udaljenost
                """)
                .bind(request.getAdresaDostave()).to("adresa")
                .bind(request.getSagaId()).to("sagaId")
                .fetch()
                .one();

        if (dostavljac.isEmpty()) {
            upisiMetriku(request, zona, null, "NEUSPESNO", null);
            posaljiOdgovor(new SagaDeliveryResponseEventDTO(
                    request.getSagaId(), "NEUSPESNO", null, "Nema slobodnog dostavljaca sa ocenom vecom od 4.5"
            ));
            return;
        }

        String dostavljacId = String.valueOf(dostavljac.get().get("id"));
        upisiMetriku(request, zona, dostavljacId, "USPESNO", izracunajMinute(request.getFondId(), zona));
        posaljiOdgovor(new SagaDeliveryResponseEventDTO(request.getSagaId(), "USPESNO", dostavljacId, null));
    }

    private void posaljiOdgovor(SagaDeliveryResponseEventDTO response) {
        try {
            Message message = MessageBuilder
                    .withBody(objectMapper.writeValueAsBytes(response))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .build();
            rabbitTemplate.send(
                    RabbitMQConfig.SAGA_EXCHANGE,
                    RabbitMQConfig.DELIVERY_RESPONSE_ROUTING_KEY,
                    message
            );
        } catch (Exception e) {
            throw new IllegalStateException("Neuspesno slanje odgovora za sagaId=" + response.getSagaId(), e);
        }
    }

    private void upisiMetriku(SagaDeliveryRequestEventDTO request, String zona, String dostavljacId,
                              String status, Double deliveryMinutes) {
        double stanjeFonda = stanjeFonda(request.getFondId()).orElse(0.0);
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        writeApi.writeMeasurement(bucket, org, WritePrecision.NS, new DeliveryAssignmentMetric(
                request.getSagaId(),
                request.getFondId(),
                request.getAdresaDostave(),
                zona,
                dostavljacId,
                status,
                deliveryMinutes,
                stanjeFonda,
                Instant.now()
        ));
    }

    private Optional<Double> stanjeFonda(String fondId) {
        return neo4jClient.query("MATCH (f:FinansijskiFond {idOriginal: $fondId}) RETURN f.ukupanIznos AS stanje")
                .bind(fondId).to("fondId")
                .fetchAs(Double.class)
                .mappedBy((typeSystem, record) -> record.get("stanje").asDouble())
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
        return stanje > 700000.0 ? baza - 4.0 : baza + 3.0;
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

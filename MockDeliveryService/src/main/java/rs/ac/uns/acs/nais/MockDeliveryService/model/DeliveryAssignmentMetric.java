package rs.ac.uns.acs.nais.MockDeliveryService.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import java.time.Instant;

@Measurement(name = "delivery_assignments")
public class DeliveryAssignmentMetric {

    @Column(tag = true)
    private String sagaId;

    @Column(tag = true)
    private String fondId;

    @Column(tag = true)
    private String adresa;

    @Column(tag = true)
    private String zona;

    @Column(tag = true)
    private String dostavljacId;

    @Column(tag = true)
    private String status;

    @Column
    private Double deliveryMinutes;

    @Column
    private Double stanjeFonda;

    @Column(timestamp = true)
    private Instant time;

    public DeliveryAssignmentMetric(String sagaId, String fondId, String adresa, String zona, String dostavljacId,
                                    String status, Double deliveryMinutes, Double stanjeFonda, Instant time) {
        this.sagaId = sagaId;
        this.fondId = fondId;
        this.adresa = adresa;
        this.zona = zona;
        this.dostavljacId = dostavljacId;
        this.status = status;
        this.deliveryMinutes = deliveryMinutes;
        this.stanjeFonda = stanjeFonda;
        this.time = time;
    }

    public String getSagaId() { return sagaId; }
    public String getFondId() { return fondId; }
    public String getAdresa() { return adresa; }
    public String getZona() { return zona; }
    public String getDostavljacId() { return dostavljacId; }
    public String getStatus() { return status; }
    public Double getDeliveryMinutes() { return deliveryMinutes; }
    public Double getStanjeFonda() { return stanjeFonda; }
    public Instant getTime() { return time; }
}

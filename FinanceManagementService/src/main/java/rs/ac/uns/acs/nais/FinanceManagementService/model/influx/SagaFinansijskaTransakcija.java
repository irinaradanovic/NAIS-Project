package rs.ac.uns.acs.nais.FinanceManagementService.model.influx;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import java.time.Instant;

@Measurement(name = "finance_saga_transakcije")
public class SagaFinansijskaTransakcija {

    @Column(tag = true)
    private String sagaId;

    @Column(tag = true)
    private String fondId;

    @Column(tag = true)
    private String tipDogadjaja; // NAPLATA ili STORNO

    @Column(tag = true)
    private String status; // FOND_NAPLACEN, ZAVRSENA, KOMPENZOVANA, ODBIJENA_NEDOVOLJNO_SREDSTAVA

    @Column
    private Double iznos;

    @Column
    private Double stanjeFondaNakon;

    @Column
    private String razlog;

    @Column(timestamp = true)
    private Instant time;

    public SagaFinansijskaTransakcija() {}

    public SagaFinansijskaTransakcija(String sagaId, String fondId, String tipDogadjaja, String status,
                                      Double iznos, Double stanjeFondaNakon, String razlog, Instant time) {
        this.sagaId = sagaId;
        this.fondId = fondId;
        this.tipDogadjaja = tipDogadjaja;
        this.status = status;
        this.iznos = iznos;
        this.stanjeFondaNakon = stanjeFondaNakon;
        this.razlog = razlog;
        this.time = time;
    }

    public String getSagaId() { return sagaId; }
    public void setSagaId(String sagaId) { this.sagaId = sagaId; }

    public String getFondId() { return fondId; }
    public void setFondId(String fondId) { this.fondId = fondId; }

    public String getTipDogadjaja() { return tipDogadjaja; }
    public void setTipDogadjaja(String tipDogadjaja) { this.tipDogadjaja = tipDogadjaja; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getIznos() { return iznos; }
    public void setIznos(Double iznos) { this.iznos = iznos; }

    public Double getStanjeFondaNakon() { return stanjeFondaNakon; }
    public void setStanjeFondaNakon(Double stanjeFondaNakon) { this.stanjeFondaNakon = stanjeFondaNakon; }

    public String getRazlog() { return razlog; }
    public void setRazlog(String razlog) { this.razlog = razlog; }

    public Instant getTime() { return time; }
    public void setTime(Instant time) { this.time = time; }
}
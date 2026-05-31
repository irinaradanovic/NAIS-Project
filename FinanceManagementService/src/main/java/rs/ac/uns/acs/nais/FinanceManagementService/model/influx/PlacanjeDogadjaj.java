package rs.ac.uns.acs.nais.FinanceManagementService.model.influx;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import java.time.Instant;

@Measurement(name = "finance_payments")
public class PlacanjeDogadjaj {

    @Column(tag = true)
    private String stanarId;

    @Column(tag = true)
    private String stanarEmail;

    @Column(tag = true)
    private String tipRacuna;

    @Column(tag = true)
    private String stanId;

    @Column
    private Double iznos;

    @Column
    private Double naVreme;

    @Column
    private Double daniKasnjenja;

    @Column(timestamp = true)
    private Instant time;

    public PlacanjeDogadjaj() {}

    public PlacanjeDogadjaj(String stanarId, String stanarEmail, String tipRacuna,
                            String stanId, Double iznos, Double naVreme,
                            Double daniKasnjenja, Instant time) {
        this.stanarId = stanarId;
        this.stanarEmail = stanarEmail;
        this.tipRacuna = tipRacuna;
        this.stanId = stanId;
        this.iznos = iznos;
        this.naVreme = naVreme;
        this.daniKasnjenja = daniKasnjenja;
        this.time = time;
    }

    public String getStanarId() { return stanarId; }
    public void setStanarId(String stanarId) { this.stanarId = stanarId; }

    public String getStanarEmail() { return stanarEmail; }
    public void setStanarEmail(String stanarEmail) { this.stanarEmail = stanarEmail; }

    public String getTipRacuna() { return tipRacuna; }
    public void setTipRacuna(String tipRacuna) { this.tipRacuna = tipRacuna; }

    public String getStanId() { return stanId; }
    public void setStanId(String stanId) { this.stanId = stanId; }

    public Double getIznos() { return iznos; }
    public void setIznos(Double iznos) { this.iznos = iznos; }

    public Double getNaVreme() { return naVreme; }
    public void setNaVreme(Double naVreme) { this.naVreme = naVreme; }

    public Double getDaniKasnjenja() { return daniKasnjenja; }
    public void setDaniKasnjenja(Double daniKasnjenja) { this.daniKasnjenja = daniKasnjenja; }

    public Instant getTime() { return time; }
    public void setTime(Instant time) { this.time = time; }
}
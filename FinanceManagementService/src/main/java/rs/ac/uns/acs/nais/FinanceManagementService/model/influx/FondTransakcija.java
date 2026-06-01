package rs.ac.uns.acs.nais.FinanceManagementService.model.influx;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import java.time.Instant;

@Measurement(name = "finance_fond_transakcije")
public class FondTransakcija {

    @Column(tag = true)
    private String fondId;

    @Column(tag = true)
    private String fondNaziv;

    @Column(tag = true)
    private String tipTransakcije;

    @Column(tag = true)
    private String stanId;

    @Column
    private Double iznos;

    @Column
    private Double ukupanIznosFonda;

    @Column(timestamp = true)
    private Instant time;

    public FondTransakcija() {}

    public FondTransakcija(String fondId, String fondNaziv, String tipTransakcije,
                           String stanId, Double iznos, Double ukupanIznosFonda, Instant time) {
        this.fondId = fondId;
        this.fondNaziv = fondNaziv;
        this.tipTransakcije = tipTransakcije;
        this.stanId = stanId;
        this.iznos = iznos;
        this.ukupanIznosFonda = ukupanIznosFonda;
        this.time = time;
    }

    public String getFondId() { return fondId; }
    public void setFondId(String fondId) { this.fondId = fondId; }

    public String getFondNaziv() { return fondNaziv; }
    public void setFondNaziv(String fondNaziv) { this.fondNaziv = fondNaziv; }

    public String getTipTransakcije() { return tipTransakcije; }
    public void setTipTransakcije(String tipTransakcije) { this.tipTransakcije = tipTransakcije; }

    public String getStanId() { return stanId; }
    public void setStanId(String stanId) { this.stanId = stanId; }

    public Double getIznos() { return iznos; }
    public void setIznos(Double iznos) { this.iznos = iznos; }

    public Double getUkupanIznosFonda() { return ukupanIznosFonda; }
    public void setUkupanIznosFonda(Double ukupanIznosFonda) { this.ukupanIznosFonda = ukupanIznosFonda; }

    public Instant getTime() { return time; }
    public void setTime(Instant time) { this.time = time; }
}
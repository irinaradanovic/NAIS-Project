package rs.ac.uns.acs.nais.FinanceManagementService.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
public class Racun {

    @Id
    private String idOriginal;

    private String tip;         // npr. "komunalije", "kirija", "struja"
    private double iznos;
    private String datumIzdavanja;   // format: "yyyy-MM-dd"
    private String rokPlacanja;      // format: "yyyy-MM-dd"
    private Boolean isPlacen;

    public Racun() {}

    public String getIdOriginal() { return idOriginal; }
    public void setIdOriginal(String idOriginal) { this.idOriginal = idOriginal; }

    public String getTip() { return tip; }
    public void setTip(String tip) { this.tip = tip; }

    public double getIznos() { return iznos; }
    public void setIznos(double iznos) { this.iznos = iznos; }

    public String getDatumIzdavanja() { return datumIzdavanja; }
    public void setDatumIzdavanja(String datumIzdavanja) { this.datumIzdavanja = datumIzdavanja; }

    public String getRokPlacanja() { return rokPlacanja; }
    public void setRokPlacanja(String rokPlacanja) { this.rokPlacanja = rokPlacanja; }

    public Boolean getIsPlacen() { return isPlacen; }
    public void setIsPlacen(Boolean isPlacen) { this.isPlacen = isPlacen; }
}

package rs.ac.uns.acs.nais.FinanceManagementService.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
public class FinansijskiFond {

    @Id
    private String idOriginal;

    private String naziv;
    private double ukupanIznos;
    private double mesecniDoprinos;
    private Boolean isAktivan;

    public FinansijskiFond() {}

    public String getIdOriginal() { return idOriginal; }
    public void setIdOriginal(String idOriginal) { this.idOriginal = idOriginal; }

    public String getNaziv() { return naziv; }
    public void setNaziv(String naziv) { this.naziv = naziv; }

    public double getUkupanIznos() { return ukupanIznos; }
    public void setUkupanIznos(double ukupanIznos) { this.ukupanIznos = ukupanIznos; }

    public double getMesecniDoprinos() { return mesecniDoprinos; }
    public void setMesecniDoprinos(double mesecniDoprinos) { this.mesecniDoprinos = mesecniDoprinos; }

    public Boolean getIsAktivan() { return isAktivan; }
    public void setIsAktivan(Boolean isAktivan) { this.isAktivan = isAktivan; }
}

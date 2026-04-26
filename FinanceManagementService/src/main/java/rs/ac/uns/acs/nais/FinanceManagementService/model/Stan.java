package rs.ac.uns.acs.nais.FinanceManagementService.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node
public class Stan {

    @Id
    private String idOriginal;

    private String adresa;
    private int brojStana;
    private double kvadratura;
    private Boolean isZauzet;

    @Relationship(value = "PRIPADA_FONDU", direction = Relationship.Direction.OUTGOING)
    private List<FinansijskiFond> fondovi = new ArrayList<>();

    public Stan() {}

    public String getIdOriginal() { return idOriginal; }
    public void setIdOriginal(String idOriginal) { this.idOriginal = idOriginal; }

    public String getAdresa() { return adresa; }
    public void setAdresa(String adresa) { this.adresa = adresa; }

    public int getBrojStana() { return brojStana; }
    public void setBrojStana(int brojStana) { this.brojStana = brojStana; }

    public double getKvadratura() { return kvadratura; }
    public void setKvadratura(double kvadratura) { this.kvadratura = kvadratura; }

    public Boolean getIsZauzet() { return isZauzet; }
    public void setIsZauzet(Boolean isZauzet) { this.isZauzet = isZauzet; }

    public List<FinansijskiFond> getFondovi() { return fondovi; }
    public void setFondovi(List<FinansijskiFond> fondovi) { this.fondovi = fondovi; }

    public void addFond(FinansijskiFond fond) { this.fondovi.add(fond); }
}

package rs.ac.uns.acs.nais.FinanceManagementService.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node
public class Stanar {

    @Id
    private Long idOriginal;

    private String ime;
    private String prezime;
    private String email;
    private String telefon;
    private Boolean isAktivan;

    @Relationship(value = "STANUJE_U", direction = Relationship.Direction.OUTGOING)
    private List<StanujeU> stanovanje = new ArrayList<>();

    @Relationship(value = "IMA_RACUN", direction = Relationship.Direction.OUTGOING)
    private List<ImaRacun> racuni = new ArrayList<>();

    public Stanar() {}

    public Long getIdOriginal() { return idOriginal; }
    public void setIdOriginal(Long idOriginal) { this.idOriginal = idOriginal; }

    public String getIme() { return ime; }
    public void setIme(String ime) { this.ime = ime; }

    public String getPrezime() { return prezime; }
    public void setPrezime(String prezime) { this.prezime = prezime; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }

    public Boolean getIsAktivan() { return isAktivan; }
    public void setIsAktivan(Boolean isAktivan) { this.isAktivan = isAktivan; }

    public List<StanujeU> getStanovanje() { return stanovanje; }
    public void setStanovanje(List<StanujeU> stanovanje) { this.stanovanje = stanovanje; }

    public List<ImaRacun> getRacuni() { return racuni; }
    public void setRacuni(List<ImaRacun> racuni) { this.racuni = racuni; }

    public void addStanovanje(StanujeU s) { this.stanovanje.add(s); }
    public void addRacun(ImaRacun r) { this.racuni.add(r); }
}

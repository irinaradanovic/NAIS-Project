package rs.ac.uns.acs.nais.FinanceManagementService.model;

import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
public class ImaRacun {

    @RelationshipId
    private Long id;

    @TargetNode
    private Racun racun;

    private String datumPlacanja;   // null ako nije placen
    private Boolean isNaVreme;      // da li je placen pre roka

    public ImaRacun() {}

    public ImaRacun(Racun racun, String datumPlacanja, Boolean isNaVreme) {
        this.racun = racun;
        this.datumPlacanja = datumPlacanja;
        this.isNaVreme = isNaVreme;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Racun getRacun() { return racun; }
    public void setRacun(Racun racun) { this.racun = racun; }

    public String getDatumPlacanja() { return datumPlacanja; }
    public void setDatumPlacanja(String datumPlacanja) { this.datumPlacanja = datumPlacanja; }

    public Boolean getIsNaVreme() { return isNaVreme; }
    public void setIsNaVreme(Boolean isNaVreme) { this.isNaVreme = isNaVreme; }
}

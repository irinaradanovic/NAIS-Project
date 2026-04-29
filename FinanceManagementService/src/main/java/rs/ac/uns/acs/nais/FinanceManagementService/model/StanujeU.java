package rs.ac.uns.acs.nais.FinanceManagementService.model;

import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
public class StanujeU {

    @RelationshipId
    private Long id;

    @TargetNode
    private Stan stan;

    private String datumUseljenja;
    private double mesecnaKirija;

    public StanujeU() {}

    public StanujeU(Stan stan, String datumUseljenja, double mesecnaKirija) {
        this.stan = stan;
        this.datumUseljenja = datumUseljenja;
        this.mesecnaKirija = mesecnaKirija;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Stan getStan() { return stan; }
    public void setStan(Stan stan) { this.stan = stan; }

    public String getDatumUseljenja() { return datumUseljenja; }
    public void setDatumUseljenja(String datumUseljenja) { this.datumUseljenja = datumUseljenja; }

    public double getMesecnaKirija() { return mesecnaKirija; }
    public void setMesecnaKirija(double mesecnaKirija) { this.mesecnaKirija = mesecnaKirija; }
}

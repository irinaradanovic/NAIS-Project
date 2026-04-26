package rs.ac.uns.acs.nais.FinanceManagementService.dto;

public class StanovanjeDTO {
    private Long stanarId;
    private String stanId;
    private String datumUseljenja;
    private double mesecnaKirija;

    public StanovanjeDTO() {}

    public Long getStanarId() { return stanarId; }
    public void setStanarId(Long stanarId) { this.stanarId = stanarId; }

    public String getStanId() { return stanId; }
    public void setStanId(String stanId) { this.stanId = stanId; }

    public String getDatumUseljenja() { return datumUseljenja; }
    public void setDatumUseljenja(String datumUseljenja) { this.datumUseljenja = datumUseljenja; }

    public double getMesecnaKirija() { return mesecnaKirija; }
    public void setMesecnaKirija(double mesecnaKirija) { this.mesecnaKirija = mesecnaKirija; }
}

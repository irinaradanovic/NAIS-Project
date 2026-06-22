package rs.ac.uns.acs.nais.FinanceManagementService.dto.saga;

public class PokreniDostavuSagaRequestDTO {

    private String fondId;
    private String adresaDostave;
    private double iznos;
    private String opis;

    public PokreniDostavuSagaRequestDTO() {}

    public String getFondId() { return fondId; }
    public void setFondId(String fondId) { this.fondId = fondId; }

    public String getAdresaDostave() { return adresaDostave; }
    public void setAdresaDostave(String adresaDostave) { this.adresaDostave = adresaDostave; }

    public double getIznos() { return iznos; }
    public void setIznos(double iznos) { this.iznos = iznos; }

    public String getOpis() { return opis; }
    public void setOpis(String opis) { this.opis = opis; }
}
package rs.ac.uns.acs.nais.MockDeliveryService.dto;

import java.time.Instant;

public class SagaDeliveryRequestEventDTO {

    private String sagaId;
    private String fondId;
    private String adresaDostave;
    private Double iznos;
    private String opis;
    private Instant vremeZahteva;

    public String getSagaId() { return sagaId; }
    public void setSagaId(String sagaId) { this.sagaId = sagaId; }
    public String getFondId() { return fondId; }
    public void setFondId(String fondId) { this.fondId = fondId; }
    public String getAdresaDostave() { return adresaDostave; }
    public void setAdresaDostave(String adresaDostave) { this.adresaDostave = adresaDostave; }
    public Double getIznos() { return iznos; }
    public void setIznos(Double iznos) { this.iznos = iznos; }
    public String getOpis() { return opis; }
    public void setOpis(String opis) { this.opis = opis; }
    public Instant getVremeZahteva() { return vremeZahteva; }
    public void setVremeZahteva(Instant vremeZahteva) { this.vremeZahteva = vremeZahteva; }
}

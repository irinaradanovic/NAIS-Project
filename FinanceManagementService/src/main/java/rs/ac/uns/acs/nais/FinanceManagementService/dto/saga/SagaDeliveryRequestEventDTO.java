package rs.ac.uns.acs.nais.FinanceManagementService.dto.saga;

import java.time.Instant;

public class SagaDeliveryRequestEventDTO {

    private String sagaId;
    private String fondId;
    private String adresaDostave;
    private double iznos;
    private String opis;
    private Instant vremeZahteva;

    public SagaDeliveryRequestEventDTO() {}

    public SagaDeliveryRequestEventDTO(String sagaId, String fondId, String adresaDostave,
                                       double iznos, String opis, Instant vremeZahteva) {
        this.sagaId = sagaId;
        this.fondId = fondId;
        this.adresaDostave = adresaDostave;
        this.iznos = iznos;
        this.opis = opis;
        this.vremeZahteva = vremeZahteva;
    }

    public String getSagaId() { return sagaId; }
    public void setSagaId(String sagaId) { this.sagaId = sagaId; }

    public String getFondId() { return fondId; }
    public void setFondId(String fondId) { this.fondId = fondId; }

    public String getAdresaDostave() { return adresaDostave; }
    public void setAdresaDostave(String adresaDostave) { this.adresaDostave = adresaDostave; }

    public double getIznos() { return iznos; }
    public void setIznos(double iznos) { this.iznos = iznos; }

    public String getOpis() { return opis; }
    public void setOpis(String opis) { this.opis = opis; }

    public Instant getVremeZahteva() { return vremeZahteva; }
    public void setVremeZahteva(Instant vremeZahteva) { this.vremeZahteva = vremeZahteva; }
}
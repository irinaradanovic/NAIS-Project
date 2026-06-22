package rs.ac.uns.acs.nais.FinanceManagementService.model.saga;

import java.time.Instant;

public class SagaDostaveState {

    private String sagaId;
    private String fondId;
    private String adresaDostave;
    private double iznos;
    private String opis;
    private SagaStatus status;
    private String dostavljacId;
    private String razlogNeuspeha;
    private Instant vremePokretanja;
    private Instant vremeAzuriranja;

    public SagaDostaveState() {}

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

    public SagaStatus getStatus() { return status; }
    public void setStatus(SagaStatus status) { this.status = status; }

    public String getDostavljacId() { return dostavljacId; }
    public void setDostavljacId(String dostavljacId) { this.dostavljacId = dostavljacId; }

    public String getRazlogNeuspeha() { return razlogNeuspeha; }
    public void setRazlogNeuspeha(String razlogNeuspeha) { this.razlogNeuspeha = razlogNeuspeha; }

    public Instant getVremePokretanja() { return vremePokretanja; }
    public void setVremePokretanja(Instant vremePokretanja) { this.vremePokretanja = vremePokretanja; }

    public Instant getVremeAzuriranja() { return vremeAzuriranja; }
    public void setVremeAzuriranja(Instant vremeAzuriranja) { this.vremeAzuriranja = vremeAzuriranja; }
}
package rs.ac.uns.acs.nais.FinanceManagementService.dto.saga;

public class SagaDeliveryResponseEventDTO {

    private String sagaId;
    private String status; // "USPESNO" ili "NEUSPESNO"
    private String dostavljacId;
    private String razlogNeuspeha;

    public SagaDeliveryResponseEventDTO() {}

    public String getSagaId() { return sagaId; }
    public void setSagaId(String sagaId) { this.sagaId = sagaId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDostavljacId() { return dostavljacId; }
    public void setDostavljacId(String dostavljacId) { this.dostavljacId = dostavljacId; }

    public String getRazlogNeuspeha() { return razlogNeuspeha; }
    public void setRazlogNeuspeha(String razlogNeuspeha) { this.razlogNeuspeha = razlogNeuspeha; }
}
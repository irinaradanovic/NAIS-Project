package rs.ac.uns.acs.nais.MockDeliveryService.dto;

public class SagaDeliveryResponseEventDTO {

    private String sagaId;
    private String status;
    private String dostavljacId;
    private String razlogNeuspeha;

    public SagaDeliveryResponseEventDTO() {}

    public SagaDeliveryResponseEventDTO(String sagaId, String status, String dostavljacId, String razlogNeuspeha) {
        this.sagaId = sagaId;
        this.status = status;
        this.dostavljacId = dostavljacId;
        this.razlogNeuspeha = razlogNeuspeha;
    }

    public String getSagaId() { return sagaId; }
    public void setSagaId(String sagaId) { this.sagaId = sagaId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDostavljacId() { return dostavljacId; }
    public void setDostavljacId(String dostavljacId) { this.dostavljacId = dostavljacId; }
    public String getRazlogNeuspeha() { return razlogNeuspeha; }
    public void setRazlogNeuspeha(String razlogNeuspeha) { this.razlogNeuspeha = razlogNeuspeha; }
}

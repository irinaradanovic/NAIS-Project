package rs.ac.uns.acs.nais.FinanceManagementService.dto;

public class PlatiRacunDTO {
    private Long stanarId;
    private String racunId;
    private String datumPlacanja;
    private boolean isNaVreme;

    public PlatiRacunDTO() {}

    public Long getStanarId() { return stanarId; }
    public void setStanarId(Long stanarId) { this.stanarId = stanarId; }

    public String getRacunId() { return racunId; }
    public void setRacunId(String racunId) { this.racunId = racunId; }

    public String getDatumPlacanja() { return datumPlacanja; }
    public void setDatumPlacanja(String datumPlacanja) { this.datumPlacanja = datumPlacanja; }

    public boolean isNaVreme() { return isNaVreme; }
    public void setNaVreme(boolean naVreme) { isNaVreme = naVreme; }
}

package rs.ac.uns.acs.nais.MockDeliveryService.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Measurement(name = "delivery_assignments")
public class DeliveryAssignmentMetric {

    @Column(tag = true)
    private String id;              // jedinstveni identifikator zapisa (UUID) - koristi se za CRUD

    @Column(tag = true)
    private String narudzbinaId;    // id narudzbine/posiljke na koju se dostava odnosi

    @Column(tag = true)
    private String adresa;          // adresa dostave

    @Column(tag = true)
    private String zona;            // zona grada u kojoj se dostava odvija

    @Column(tag = true)
    private String dostavljacId;    // id dostavljaca kome je dostava dodeljena

    @Column(tag = true)
    private String status;          // status dostave (npr. USPESNO, NEUSPESNO)

    @Column
    private Double deliveryMinutes; // trajanje dostave u minutima

    @Column(timestamp = true)
    private Instant time;           // vreme nastanka zapisa
}
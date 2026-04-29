package rs.ac.uns.acs.nais.FinanceManagementService.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.FinanceManagementService.dto.PlatiRacunDTO;
import rs.ac.uns.acs.nais.FinanceManagementService.dto.StanovanjeDTO;
import rs.ac.uns.acs.nais.FinanceManagementService.model.Stanar;
import rs.ac.uns.acs.nais.FinanceManagementService.repository.StanarRepository;
import rs.ac.uns.acs.nais.FinanceManagementService.service.IStanarService;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StanarService implements IStanarService {

    private final StanarRepository stanarRepository;
    private final Neo4jClient neo4jClient;

    public StanarService(StanarRepository stanarRepository, Neo4jClient neo4jClient) {
        this.stanarRepository = stanarRepository;
        this.neo4jClient = neo4jClient;
    }

    @Override
    public List<Stanar> findAll() {
        return stanarRepository.findAll();
    }

    @Override
    public Stanar addStanar(Stanar stanar) {
        stanar.setIsAktivan(true);
        return stanarRepository.save(stanar);
    }

    @Override
    public boolean deleteStanar(String email) {
        Stanar s = stanarRepository.findByEmail(email);
        if (s != null) {
            s.setIsAktivan(false);
            stanarRepository.save(s);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateEmail(String stariEmail, String noviEmail) {
        Stanar s = stanarRepository.findByEmail(stariEmail);
        if (s != null) {
            s.setEmail(noviEmail);
            stanarRepository.save(s);
            return true;
        }
        return false;
    }

    @Override
    public void dodajStanovanje(StanovanjeDTO dto) {
        stanarRepository.dodajStanovanje(dto.getStanarId(), dto.getStanId(),
                dto.getDatumUseljenja(), dto.getMesecnaKirija());
    }

    @Override
    public void azurirajKiriju(Long stanarId, String stanId, double novaKirija) {
        stanarRepository.azurirajKiriju(stanarId, stanId, novaKirija);
    }

    @Override
    public void ukloniStanovanje(Long stanarId, String stanId) {
        stanarRepository.ukloniStanovanje(stanarId, stanId);
    }

    @Override
    public void dodajRacun(Long stanarId, String racunId) {
        stanarRepository.dodajRacun(stanarId, racunId);
    }

    @Override
    public void platiRacun(PlatiRacunDTO dto) {
        stanarRepository.platiRacun(dto.getStanarId(), dto.getRacunId(),
                dto.getDatumPlacanja(), dto.isNaVreme());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> stanariSaDugovima() {
        String query = """
                MATCH (st:Stanar)-[ir:IMA_RACUN]->(r:Racun)
                WHERE r.isPlacen = false AND st.isAktivan = true
                WITH st, COUNT(r) AS brojNeplacenih, SUM(r.iznos) AS ukupanDug
                WHERE brojNeplacenih > 0
                RETURN st.ime AS ime, st.prezime AS prezime, st.email AS email,
                       brojNeplacenih, ROUND(ukupanDug, 2) AS ukupanDug
                ORDER BY ukupanDug DESC
                """;

        return neo4jClient.query(query)
                .fetchAs(Map.class)
                .mappedBy((typeSystem, record) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("ime", record.get("ime").asString());
                    row.put("prezime", record.get("prezime").asString());
                    row.put("email", record.get("email").asString());
                    row.put("brojNeplacenih", record.get("brojNeplacenih").asLong());
                    row.put("ukupanDug", record.get("ukupanDug").asDouble());
                    return row;
                })
                .all()
                .stream()
                .map(m -> (Map<String, Object>) m)
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> prosecnaKirijaPoAdresi() {
        String query = """
                MATCH (st:Stanar)-[r:STANUJE_U]->(s:Stan)
                WHERE st.isAktivan = true
                WITH s.adresa AS adresa, AVG(r.mesecnaKirija) AS prosecnaKirija, COUNT(st) AS brojStanara
                WHERE brojStanara > 0
                RETURN adresa, ROUND(prosecnaKirija, 2) AS prosecnaKirija, brojStanara
                ORDER BY prosecnaKirija DESC
                """;

        return neo4jClient.query(query)
                .fetchAs(Map.class)
                .mappedBy((typeSystem, record) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("adresa", record.get("adresa").asString());
                    row.put("prosecnaKirija", record.get("prosecnaKirija").asDouble());
                    row.put("brojStanara", record.get("brojStanara").asLong());
                    return row;
                })
                .all()
                .stream()
                .map(m -> (Map<String, Object>) m)
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> stanariIspodProsecneKirije() {
        String query = """
                MATCH (st:Stanar)-[r:STANUJE_U]->(s:Stan)
                WHERE st.isAktivan = true
                WITH s.adresa AS adresa, AVG(r.mesecnaKirija) AS prosecnaKirijaNaAdresi
                MATCH (st2:Stanar)-[r2:STANUJE_U]->(s2:Stan)
                WHERE s2.adresa = adresa AND r2.mesecnaKirija < prosecnaKirijaNaAdresi
                WITH st2, r2.mesecnaKirija AS trenutnaKirija, prosecnaKirijaNaAdresi,
                     ROUND(prosecnaKirijaNaAdresi - r2.mesecnaKirija, 2) AS razlika
                WHERE razlika > 0
                RETURN st2.ime AS ime, st2.prezime AS prezime,
                       trenutnaKirija, ROUND(prosecnaKirijaNaAdresi, 2) AS prosecnaKirija, razlika
                ORDER BY razlika DESC
                """;

        return neo4jClient.query(query)
                .fetchAs(Map.class)
                .mappedBy((typeSystem, record) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("ime", record.get("ime").asString());
                    row.put("prezime", record.get("prezime").asString());
                    row.put("trenutnaKirija", record.get("trenutnaKirija").asDouble());
                    row.put("prosecnaKirija", record.get("prosecnaKirija").asDouble());
                    row.put("razlika", record.get("razlika").asDouble());
                    return row;
                })
                .all()
                .stream()
                .map(m -> (Map<String, Object>) m)
                .collect(Collectors.toList());
    }

    @Override
    public byte[] exportIzvestaj() throws IOException {
        List<Map<String, Object>> dugovi = stanariSaDugovima();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Font.BOLD);
        Paragraph title = new Paragraph("IZVESTAJ: STANARI SA DUGOVIMA", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2f, 2f, 3f, 2f, 2f});

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.BOLD);
        Color headerColor = new Color(52, 152, 219);

        for (String col : new String[]{"Ime", "Prezime", "Email", "Br. neplacenih", "Ukupan dug (RSD)"}) {
            PdfPCell cell = new PdfPCell(new Phrase(col, headerFont));
            cell.setBackgroundColor(headerColor);
            cell.setPadding(6);
            table.addCell(cell);
        }

        Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        for (Map<String, Object> row : dugovi) {
            table.addCell(new PdfPCell(new Phrase(String.valueOf(row.get("ime")), rowFont)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(row.get("prezime")), rowFont)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(row.get("email")), rowFont)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(row.get("brojNeplacenih")), rowFont)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(row.get("ukupanDug")), rowFont)));
        }

        document.add(table);
        document.close();
        return out.toByteArray();
    }
}
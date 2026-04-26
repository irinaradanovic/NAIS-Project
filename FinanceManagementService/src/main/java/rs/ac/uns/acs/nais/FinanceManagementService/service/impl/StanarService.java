package rs.ac.uns.acs.nais.FinanceManagementService.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.FinanceManagementService.dto.PlatiRacunDTO;
import rs.ac.uns.acs.nais.FinanceManagementService.dto.StanovanjeDTO;
import rs.ac.uns.acs.nais.FinanceManagementService.model.Stanar;
import rs.ac.uns.acs.nais.FinanceManagementService.repository.StanarRepository;
import rs.ac.uns.acs.nais.FinanceManagementService.service.IStanarService;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class StanarService implements IStanarService {

    private final StanarRepository stanarRepository;

    public StanarService(StanarRepository stanarRepository) {
        this.stanarRepository = stanarRepository;
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
    public List<Map<String, Object>> stanariSaDugovima() {
        return stanarRepository.stanariSaDugovima();
    }

    @Override
    public List<Map<String, Object>> prosecnaKirijaPoAdresi() {
        return stanarRepository.prosecnaKirijaPoAdresi();
    }

    @Override
    public List<Map<String, Object>> stanariIspodProsecneKirije() {
        return stanarRepository.stanariIspodProsecneKirije();
    }

    /**
     * Generise PDF izvestaj stanara sa dugovima.
     * Prikazuje ime, prezime, email, broj neplacenih racuna i ukupan dug.
     */
    @Override
    public byte[] exportIzvestaj() throws IOException {
        List<Map<String, Object>> dugovi = stanarRepository.stanariSaDugovima();

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

package rs.ac.uns.acs.nais.FinanceManagementService.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;

@Component
public class FinanceDemoDataSeeder implements CommandLineRunner {

    private final Neo4jClient neo4jClient;

    public FinanceDemoDataSeeder(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    @Override
    public void run(String... args) {
        neo4jClient.query("""
                MERGE (fond1:FinansijskiFond {idOriginal: 'FOND-001'})
                  SET fond1.naziv = 'Fond za tekuce odrzavanje',
                      fond1.ukupanIznos = coalesce(fond1.ukupanIznos, 450000.0),
                      fond1.mesecniDoprinos = 1500.0,
                      fond1.isAktivan = true
                MERGE (fond2:FinansijskiFond {idOriginal: 'FOND-002'})
                  SET fond2.naziv = 'Rezervni fond',
                      fond2.ukupanIznos = coalesce(fond2.ukupanIznos, 820000.0),
                      fond2.mesecniDoprinos = 2000.0,
                      fond2.isAktivan = true
                MERGE (fond3:FinansijskiFond {idOriginal: 'FOND-003'})
                  SET fond3.naziv = 'Fond za investiciono odrzavanje',
                      fond3.ukupanIznos = coalesce(fond3.ukupanIznos, 1200000.0),
                      fond3.mesecniDoprinos = 3500.0,
                      fond3.isAktivan = true
                MERGE (fond4:FinansijskiFond {idOriginal: 'FOND-004'})
                  SET fond4.naziv = 'Fond za hitne intervencije',
                      fond4.ukupanIznos = coalesce(fond4.ukupanIznos, 150000.0),
                      fond4.mesecniDoprinos = 500.0,
                      fond4.isAktivan = false
                """).run();
    }
}

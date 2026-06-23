package rs.ac.uns.acs.nais.MockDeliveryService.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;

@Component
public class DeliverySeeder implements CommandLineRunner {

    private final Neo4jClient neo4jClient;

    public DeliverySeeder(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    @Override
    public void run(String... args) {
        neo4jClient.query("""
                MERGE (d1:Dostavljac {id: 'DOST-001'})
                  SET d1.ime = 'Milos', d1.status = 'SLOBODAN', d1.prosecnaOcena = 4.8, d1.zona = 'Bulevar Oslobodjenja'
                MERGE (d2:Dostavljac {id: 'DOST-002'})
                  SET d2.ime = 'Sara', d2.status = 'SLOBODAN', d2.prosecnaOcena = 4.7, d2.zona = 'Jovana Subotica'
                MERGE (d3:Dostavljac {id: 'DOST-003'})
                  SET d3.ime = 'Ivan', d3.status = 'ZAUZET', d3.prosecnaOcena = 4.9, d3.zona = 'Futoska'
                MERGE (d4:Dostavljac {id: 'DOST-004'})
                  SET d4.ime = 'Tara', d4.status = 'SLOBODAN', d4.prosecnaOcena = 4.3, d4.zona = 'Centar'
                """).run();
    }
}

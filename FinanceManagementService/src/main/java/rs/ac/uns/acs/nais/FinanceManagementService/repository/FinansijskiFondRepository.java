package rs.ac.uns.acs.nais.FinanceManagementService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.FinanceManagementService.model.FinansijskiFond;

import java.util.List;

@Repository
public interface FinansijskiFondRepository extends Neo4jRepository<FinansijskiFond, String> {

    List<FinansijskiFond> findByIsAktivan(Boolean isAktivan);

    FinansijskiFond findByNaziv(String naziv);
}

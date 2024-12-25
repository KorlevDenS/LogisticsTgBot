package korolev.dens.logisticstgbot.repositories;

import korolev.dens.logisticstgbot.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Integer> {
}
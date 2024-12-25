package korolev.dens.logisticstgbot.repositories;

import korolev.dens.logisticstgbot.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Integer> {

    Integer countByCreator(Integer creator);
    List<Client> findAllByCreator(Integer creator);
    Optional<Client> findByCreatorAndNumber(Integer creator, Integer number);

}
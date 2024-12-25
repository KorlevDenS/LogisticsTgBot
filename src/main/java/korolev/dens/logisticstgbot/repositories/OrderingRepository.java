package korolev.dens.logisticstgbot.repositories;

import korolev.dens.logisticstgbot.model.Client;
import korolev.dens.logisticstgbot.model.Ordering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderingRepository extends JpaRepository<Ordering, Integer> {

    List<Ordering> findAllByCustomer(Client customer);
    List<Ordering> findAllByTransporter(Client transporter);

    @Query(value = "select * from ordering where status = '2' and customer = :customer", nativeQuery = true)
    List<Ordering> findAllCompletedByCustomer(@Param("customer") Integer customer);

    @Query(value = "select * from ordering where status <> '2' and customer = :customer", nativeQuery = true)
    List<Ordering> findAllUncompletedByCustomer(@Param("customer") Integer customer);

    @Query(value = "select * from ordering where status = '0'", nativeQuery = true)
    List<Ordering> findAllAvailable();
}
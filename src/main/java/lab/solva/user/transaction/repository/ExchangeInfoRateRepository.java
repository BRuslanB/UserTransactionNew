package lab.solva.user.transaction.repository;

import lab.solva.user.transaction.model.ExchangeInfoRateEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ExchangeInfoRateRepository extends CassandraRepository<ExchangeInfoRateEntity, String> {

    // Returns a record of the specified date of a closed exchange rate (defaults to the current date)
    @Query("SELECT * FROM exchange_info_rate WHERE request_date = :requestDate AND is_latest = true")
    Set<ExchangeInfoRateEntity> findExchangeInfoRateByRequestDate(LocalDate requestDate);
    default Optional<ExchangeInfoRateEntity> findCurrentExchangeInfoRate(LocalDate requestDate) {
        return findExchangeInfoRateByRequestDate(requestDate).stream().findFirst();
    }

    // Returns the last date of the closed exchange rate
    @Query("SELECT MAX(request_date) FROM exchange_info_rate WHERE is_latest = true ALLOW FILTERING")
    LocalDate findLatestRequestDate();

    // Returns a record of the last date of a closed exchange rate
    @Query("SELECT * FROM exchange_info_rate WHERE request_date = :latestRequestDate")
    Set<ExchangeInfoRateEntity> findExchangeInfoRateByIsLatest(LocalDate latestRequestDate);
    default Optional<ExchangeInfoRateEntity> findLatestExchangeInfoRate(LocalDate latestRequestDate) {
        return findExchangeInfoRateByIsLatest(latestRequestDate).stream().findFirst();
    }
}

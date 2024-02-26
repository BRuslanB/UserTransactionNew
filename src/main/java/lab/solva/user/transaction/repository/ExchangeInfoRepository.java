package lab.solva.user.transaction.repository;

import jakarta.transaction.Transactional;
import lab.solva.user.transaction.model.ExchangeInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional
public interface ExchangeInfoRepository extends JpaRepository<ExchangeInfoEntity, Long>  {

    // Returns a record of the last date of the exchange rate
    ExchangeInfoEntity findFirstByOrderByRequestDateDesc();
    default Optional<ExchangeInfoEntity> findLatestExchangeInfo() {
        return Optional.ofNullable(findFirstByOrderByRequestDateDesc());
    }

    // Returns a record of the specified exchange rate date (by default, the current date)
    @Query("SELECT c FROM ExchangeInfoEntity c WHERE c.requestDate = :requestDate")
    Set<ExchangeInfoEntity> findExchangeInfoByRequestDate(LocalDate requestDate);
    default Optional<ExchangeInfoEntity> findCurrentExchangeInfo(LocalDate requestDate) {
        return findExchangeInfoByRequestDate(requestDate).stream().findFirst();
    }
}

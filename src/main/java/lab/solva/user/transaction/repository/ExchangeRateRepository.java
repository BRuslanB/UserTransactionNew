package lab.solva.user.transaction.repository;

import jakarta.transaction.Transactional;
import lab.solva.user.transaction.model.ExchangeRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@Transactional
public interface ExchangeRateRepository extends JpaRepository<ExchangeRateEntity, Long> {

    // Returns exchange rates for the specified entry
    @Query("SELECT c FROM ExchangeRateEntity c WHERE c.exchangeInfoEntity.id = :exchangeInfoId")
    Set<ExchangeRateEntity> findAllExchangeRate(Long exchangeInfoId);
}

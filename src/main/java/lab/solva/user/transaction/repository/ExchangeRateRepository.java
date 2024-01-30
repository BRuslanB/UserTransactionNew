package lab.solva.user.transaction.repository;

import lab.solva.user.transaction.model.ExchangeRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Set;

@Repository
@Transactional
public interface ExchangeRateRepository extends JpaRepository<ExchangeRateEntity, Long> {

    // Возвращает курсы валют по указанной записи
    @Query("SELECT c FROM ExchangeRateEntity c WHERE c.exchangeInfoEntity.id = :exchangeInfoId")
    Set<ExchangeRateEntity> findAllExchangeRate(Long exchangeInfoId);
}

package lab.solva.user.transaction.repository;

import lab.solva.user.transaction.model.ExchangeInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional
public interface ExchangeInfoRepository extends JpaRepository<ExchangeInfoEntity, Long>  {

    // Возвращает запись о последней дате курса валют
    ExchangeInfoEntity findFirstByOrderByRequestDateDesc();
    default Optional<ExchangeInfoEntity> findLatestExchangeInfo() {
        return Optional.ofNullable(findFirstByOrderByRequestDateDesc());
    }

    // Возвращает запись об указанной дате курса валют (по умолчанию на текущую дату)
    @Query("SELECT c FROM ExchangeInfoEntity c WHERE c.requestDate = :requestDate")
    Set<ExchangeInfoEntity> findExchangeInfoByRequestDate(LocalDate requestDate);
    default Optional<ExchangeInfoEntity> findCurrentExchangeInfo(LocalDate requestDate) {
        return findExchangeInfoByRequestDate(requestDate).stream().findFirst();
    }
}

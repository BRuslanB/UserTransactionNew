package lab.solva.user.transaction.repository;

import lab.solva.user.transaction.model.ExchangeInfoEntity;
import lab.solva.user.transaction.model.ExchangeRateEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional
public interface ExchangeInfoRepository extends JpaRepository<ExchangeInfoEntity, Long>  {

    ExchangeInfoEntity findFirstByOrderByRequestDateDesc();

    default Optional<ExchangeInfoEntity> findLatestExchangeInfo() {
        return Optional.ofNullable(findFirstByOrderByRequestDateDesc());
    }

    @Query("SELECT c FROM ExchangeInfoEntity c WHERE c.requestDate = :requestDate")
    Set<ExchangeInfoEntity> findByRequestDate(LocalDate requestDate);

    default Optional<ExchangeInfoEntity> findByExchangeInfo(LocalDate requestDate) {
        return findByRequestDate(requestDate).stream().findFirst();
    }
}

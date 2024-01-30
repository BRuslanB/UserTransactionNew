package lab.solva.user.transaction.repository;

import lab.solva.user.transaction.model.AmountLimitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface AmountLimitRepository extends JpaRepository<AmountLimitEntity, Long>  {

    // Возвращает список всех лимитов клиента
    @Query("SELECT c FROM AmountLimitEntity c " +
            "WHERE c.accountClient = :accountClient " +
            "ORDER BY c.limitDateTime DESC")
    List<AmountLimitEntity> findAllAmountLimitByAccount(String accountClient);

    // Возвращает последний установленный лимит клиента по категории затрат за текущий месяц
    @Query("SELECT c FROM AmountLimitEntity c " +
            "WHERE c.accountClient = :accountClient " +
                "AND c.expenseCategory = :expenseCategory " +
                "AND MONTH(c.limitDateTime) = :month AND YEAR(c.limitDateTime) = :year " +
            "ORDER BY c.limitDateTime DESC")
    List<AmountLimitEntity> findAmountLimitByAccountAndCategoryAndMonth(String accountClient,
            String expenseCategory, int month, int year);
    default Optional<AmountLimitEntity> findAmountLimit(String accountClient, String expenseCategory, int month, int year) {
        return findAmountLimitByAccountAndCategoryAndMonth(accountClient, expenseCategory, month, year).stream().findFirst();
    }
}

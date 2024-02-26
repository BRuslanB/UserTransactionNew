package lab.solva.user.transaction.repository;

import jakarta.transaction.Transactional;
import lab.solva.user.transaction.model.AmountLimitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface AmountLimitRepository extends JpaRepository<AmountLimitEntity, Long>  {

    // Returns a list of all client limits
    @Query("SELECT c FROM AmountLimitEntity c " +
            "WHERE c.accountClient = :accountClient " +
            "ORDER BY c.limitDateTime DESC")
    List<AmountLimitEntity> findAllAmountLimitByAccount(String accountClient);

    // Returns the customer's last set limit by cost category for the current month
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

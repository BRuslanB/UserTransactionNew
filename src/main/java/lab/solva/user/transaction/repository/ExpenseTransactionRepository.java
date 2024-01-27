package lab.solva.user.transaction.repository;

import lab.solva.user.transaction.model.ExpenseTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface ExpenseTransactionRepository extends JpaRepository<ExpenseTransactionEntity, Long>  {

    @Query("SELECT t.accountClient, " +
                "t.accountCounterparty, " +
                "t.currencyCode, " +
                "t.transactionSum, " +
                "t.expenseCategory, " +
                "t.transactionDateTime, " +
                "l.limitSum, " +
                "l.limitDateTime, " +
                "l.limitCurrencyCode " +
            "FROM ExpenseTransactionEntity t " +
            "JOIN t.amountLimitEntity l " +
            "WHERE t.limitExceeded = true " +
            "ORDER BY t.transactionDateTime ASC")
    List<Object[]> findAllTransactionWithExceededLimit();

    @Query(value = "SELECT SUM(c.transactionSum) " +
            "FROM ExpenseTransactionEntity c " +
            "WHERE c.accountClient = :accountClient " +
                "AND c.expenseCategory = :expenseCategory " +
                "AND c.currencyCode = :currencyCode " +
                "AND MONTH(c.transactionDateTime) = :month AND YEAR(c.transactionDateTime) = :year")
    double calcTransactionSum(String accountClient, String expenseCategory, String currencyCode, int month, int year);
}

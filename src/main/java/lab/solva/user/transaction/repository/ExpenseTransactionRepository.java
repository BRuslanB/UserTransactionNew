package lab.solva.user.transaction.repository;

import jakarta.transaction.Transactional;
import lab.solva.user.transaction.model.ExpenseTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface ExpenseTransactionRepository extends JpaRepository<ExpenseTransactionEntity, Long>  {

    // Getting a list of transactions that have exceeded the limit, returns an array of Entity field objects
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
            "WHERE t.accountClient = :accountClient " +
                "AND t.limitExceeded = true " +
            "ORDER BY t.transactionDateTime ASC")
    List<Object[]> findAllTransactionWithExceededLimit(String accountClient);

    // Returns the sum of all transactions according to the condition;
    // If there are no records, it will return the value 0
    @Query(value = "SELECT COALESCE(SUM(c.transactionSum), 0) " +
            "FROM ExpenseTransactionEntity c " +
            "WHERE c.accountClient = :accountClient " +
                "AND c.expenseCategory = :expenseCategory " +
                "AND c.currencyCode = :currencyCode " +
                "AND MONTH(c.transactionDateTime) = :month AND YEAR(c.transactionDateTime) = :year")
    double calcTransactionSum(String accountClient, String expenseCategory, String currencyCode, int month, int year);
//    double calcTransactionSum(String accountClient, String expenseCategory, String currencyCode, int month, int year) {
//        log.info("Вызван метод calcTransactionSum с параметрами: " +
//                        "accountClient={}, expenseCategory={}, currencyCode={}, month={}, year={}",
//                accountClient, expenseCategory, currencyCode, month, year);
//        // Остальной код метода...
//    };
}

package lab.solva.user.transaction.repository;

import lab.solva.user.transaction.dto.TransactionExceededLimitDto;
import lab.solva.user.transaction.model.ExpenseTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface ExpenseTransactionRepository extends JpaRepository<ExpenseTransactionEntity, Long>  {

//    @Query("SELECT new lab.solva.user.transaction.dto.TransactionExceededLimitDto(" +
//            "t.accountClient, t.accountCounterparty, t.currencyCode, t.transactionSum, " +
//            "t.expenseCategory, t.transactionDateTime, l.limitSum, l.limitDateTime, l.limitCurrencyCode) " +
//            "FROM ExpenseTransactionEntity t JOIN AmountLimitEntity l " +
//            "ON t.expenseCategory = l.expenseCategory " +
//            "WHERE t.transactionDateTime >= l.limitDateTime " +
//            "ORDER BY t.transactionDateTime ASC")
//    List<TransactionExceededLimitDto> findTransactionsWithLimits();
}

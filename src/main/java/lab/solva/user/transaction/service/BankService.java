package lab.solva.user.transaction.service;

import lab.solva.user.transaction.dto.ExpenseTransactionDto;
import lab.solva.user.transaction.model.ExpenseTransactionEntity;

public interface BankService {

    // Saving a transaction to the database
    ExpenseTransactionEntity saveExpenseTransactionDto(ExpenseTransactionDto expenseTransactionDto);
}

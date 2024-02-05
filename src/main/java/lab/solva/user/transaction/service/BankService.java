package lab.solva.user.transaction.service;

import lab.solva.user.transaction.dto.ExpenseTransactionDto;

public interface BankService {

    // Saving a transaction to the database
    void saveExpenseTransactionDto(ExpenseTransactionDto expenseTransactionDto);
}

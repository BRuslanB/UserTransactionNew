package lab.solva.user.transaction.service;

import lab.solva.user.transaction.dto.ExpenseTransactionDto;

public interface BankService {

    void saveExpenseTransactionDto(ExpenseTransactionDto expenseTransactionDto);
}

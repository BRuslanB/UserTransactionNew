package lab.solva.user.transaction.service;

import lab.solva.user.transaction.dto.ExpenseTransactionDto;

public interface BankService {

    // Сохранение транзакции в БД
    void saveExpenseTransactionDto(ExpenseTransactionDto expenseTransactionDto);
}

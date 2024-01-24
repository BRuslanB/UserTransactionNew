package lab.solva.user.transaction.service.impl;

import lab.solva.user.transaction.dto.ExpenseTransactionDto;
import lab.solva.user.transaction.model.ExpenseTransactionEntity;
import lab.solva.user.transaction.repository.ExpenseTransactionRepository;
import lab.solva.user.transaction.service.BankService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankServiceImpl implements BankService {

    private final ExpenseTransactionRepository expenseTransactionRepository;
    @Override
    public void saveExpenseTransactionDto(ExpenseTransactionDto expenseTransactionDto) {
        // Сохранение полученных данных из expenseTransactionDto
        if (expenseTransactionDto != null) {
            ExpenseTransactionEntity expenseTransactionEntity = new ExpenseTransactionEntity();
            expenseTransactionEntity.setAccountClient(expenseTransactionDto.account_to);
            expenseTransactionEntity.setAccountCounterparty(expenseTransactionDto.account_from);
            expenseTransactionEntity.setCurrencyCode(expenseTransactionDto.currency_shortname);
            expenseTransactionEntity.setTransactionSum(expenseTransactionDto.Sum);
            expenseTransactionEntity.setExpenseCategory(expenseTransactionDto.expense_category);
            expenseTransactionEntity.setTransactionDateTime(expenseTransactionDto.datetime);
            expenseTransactionEntity.setLimitExceeded(false);
            // Логирование действия
            expenseTransactionRepository.save(expenseTransactionEntity);
        }
    }
}

package lab.solva.user.transaction.service.impl;

import lab.solva.user.transaction.dto.ExpenseTransactionDto;
import lab.solva.user.transaction.service.BankService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankServiceImpl implements BankService {

    @Override
    public void saveExpenseTransactionDto(ExpenseTransactionDto expenseTransactionDto) {

    }
}

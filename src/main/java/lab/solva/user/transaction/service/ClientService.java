package lab.solva.user.transaction.service;

import lab.solva.user.transaction.dto.AmountLimitDto;
import lab.solva.user.transaction.dto.ExpenseTransactionDto;

import java.util.List;

public interface ClientService {

    List<AmountLimitDto> getAllAmountLimitDto();
    void setAmountLimitDto(AmountLimitDto amountLimitDto);
    List<ExpenseTransactionDto> getTransactionExceededLimitDto();
}

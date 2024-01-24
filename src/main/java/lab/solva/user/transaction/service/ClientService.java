package lab.solva.user.transaction.service;

import lab.solva.user.transaction.dto.AmountLimitDateDto;
import lab.solva.user.transaction.dto.AmountLimitDto;
import lab.solva.user.transaction.dto.ExpenseTransactionDto;
import lab.solva.user.transaction.dto.TransactionExceededLimitDto;

import java.util.List;

public interface ClientService {

    List<AmountLimitDateDto> getAllAmountLimitDateDto();
    void setAmountLimitDto(AmountLimitDto amountLimitDto);
    List<TransactionExceededLimitDto> getTransactionExceededLimitDto();
}

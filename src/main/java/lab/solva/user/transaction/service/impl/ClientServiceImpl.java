package lab.solva.user.transaction.service.impl;

import lab.solva.user.transaction.dto.AmountLimitDto;
import lab.solva.user.transaction.dto.ExpenseTransactionDto;
import lab.solva.user.transaction.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    @Override
    public List<AmountLimitDto> getAllAmountLimitDto() {
        return null;
    }

    @Override
    public void setAmountLimitDto(AmountLimitDto amountLimitDto) {

    }

    @Override
    public List<ExpenseTransactionDto> getTransactionExceededLimitDto() {
        return null;
    }
}

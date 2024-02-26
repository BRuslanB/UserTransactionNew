package lab.solva.user.transaction.service;

import lab.solva.user.transaction.dto.AmountLimitDateDto;
import lab.solva.user.transaction.dto.AmountLimitDto;
import lab.solva.user.transaction.dto.TransactionExceededLimitDto;
import lab.solva.user.transaction.model.AmountLimitEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ClientService {

    // Retrieving all limits from the database in Dto format
    List<AmountLimitDateDto> getAllAmountLimitDateDtoByAccountClient(String accountClient);

    // Setting and saving a limit in the database
    AmountLimitEntity saveAmountLimitDto(AmountLimitDto amountLimitDto);

    // Obtaining a list of all transactions that exceeded the established limit from the database in Dto format
    List<TransactionExceededLimitDto> getAllTransactionExceededLimitDtoByAccountClient(String accountClient);
}

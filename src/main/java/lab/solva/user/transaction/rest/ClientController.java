package lab.solva.user.transaction.rest;

import lab.solva.user.transaction.dto.AmountLimitDateDto;
import lab.solva.user.transaction.dto.AmountLimitDto;
import lab.solva.user.transaction.dto.TransactionExceededLimitDto;
import lab.solva.user.transaction.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/client")
@CrossOrigin
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping(value = "{account_client}")
    // Получение всех лимитов из БД
    public List<AmountLimitDateDto> getAllAmountLimitDateByAccountClient
            (@PathVariable(name = "account_client") String accountClient) {
        // Логирование действия
        return clientService.getAllAmountLimitDateDtoByAccountClient(accountClient);
    }

    @GetMapping(value = "transaction/{account_client}")
    // Получение списка всех транзакции превысивших установленный лимит из БД
    public List<TransactionExceededLimitDto> getAllTransactionExceededLimitByAccountClient(
            @PathVariable(name = "account_client") String accountClient) {
        // Логирование действия
        return clientService.getAllTransactionExceededLimitDtoByAccountClient(accountClient);
    }

    @PostMapping
    // Установка и сохранение лимита в БД
    public void setAmountLimit(@RequestBody AmountLimitDto amountLimitDto){
        // Логирование действия
        clientService.setAmountLimitDto(amountLimitDto);
    }
}

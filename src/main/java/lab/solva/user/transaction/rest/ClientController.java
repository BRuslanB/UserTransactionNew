package lab.solva.user.transaction.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lab.solva.user.transaction.dto.AmountLimitDateDto;
import lab.solva.user.transaction.dto.AmountLimitDto;
import lab.solva.user.transaction.dto.TransactionExceededLimitDto;
import lab.solva.user.transaction.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/client")
@CrossOrigin
@Log4j2
@RequiredArgsConstructor
@Tag(name = "Client", description = "All methods of Client Services")
public class ClientController {

    private final ClientService clientService;

    @GetMapping(value = "{account_client}")
    @Operation(description = "Getting all limits from the database")
    public List<AmountLimitDateDto> getAllAmountLimitDateByAccountClient
            (@PathVariable(name = "account_client") String accountClient) {

        log.debug("!Call method getting all limits from the database");
        return clientService.getAllAmountLimitDateDtoByAccountClient(accountClient);
    }

    @GetMapping(value = "transaction/{account_client}")
    @Operation(description = "Retrieving a list of all transactions that exceeded the specified limit from the database")
    public List<TransactionExceededLimitDto> getAllTransactionExceededLimitByAccountClient(
            @PathVariable(name = "account_client") String accountClient) {

        log.debug("!Call method getting a list of all transactions that exceeded the established limit from the database");
        return clientService.getAllTransactionExceededLimitDtoByAccountClient(accountClient);
    }

    @PostMapping
    @Operation(description = "Setting and saving a limit in the database")
    public void setAmountLimit(@RequestBody AmountLimitDto amountLimitDto){

        log.debug("!Call method setting and saving a limit in the database");
        clientService.setAmountLimitDto(amountLimitDto);
    }
}

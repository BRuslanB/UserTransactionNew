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

    @GetMapping
    public List<AmountLimitDateDto> getAllAmountLimitDate() {
        // Логирование действия
        return clientService.getAllAmountLimitDateDto();
    }

    @GetMapping(value = "transaction")
    public List<TransactionExceededLimitDto> getTransactionExceededLimit() {
        // Логирование действия
        return clientService.getTransactionExceededLimitDto();
    }

    @PostMapping
    public void setAmountLimit(@RequestBody AmountLimitDto amountLimitDto){
        // Логирование действия
        clientService.setAmountLimitDto(amountLimitDto);
    }
}

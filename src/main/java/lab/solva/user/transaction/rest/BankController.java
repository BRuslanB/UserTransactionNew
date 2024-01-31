package lab.solva.user.transaction.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lab.solva.user.transaction.dto.ExpenseTransactionDto;
import lab.solva.user.transaction.service.BankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/bank")
@CrossOrigin
@Log4j2
@RequiredArgsConstructor
@Tag(name = "Bank", description = "All methods of Bank Services")
public class BankController {

    private final BankService bankService;

    @PostMapping
    @Operation(description = "Saving a transaction from an external service to the database")
    public void saveExpenseTransaction(@RequestBody ExpenseTransactionDto expenseTransactionDto){

        log.debug("!Call method saving a transaction from an external service to the database");
        bankService.saveExpenseTransactionDto(expenseTransactionDto);
    }
}

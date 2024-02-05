package lab.solva.user.transaction.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lab.solva.user.transaction.dto.ExpenseTransactionDto;
import lab.solva.user.transaction.service.BankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/bank")
@CrossOrigin
@RequiredArgsConstructor
@Log4j2
@Tag(name = "Bank", description = "All methods of Bank Services")
public class BankController {

    private final BankService bankService;

    @SuppressWarnings("unused")
    @PostMapping
    @Operation(description = "Saving a Transaction to the Database")
    public ResponseEntity<Object> saveExpenseTransaction(@RequestBody ExpenseTransactionDto expenseTransactionDto){

        log.debug("!Call method saving a Transaction to the Database");
        bankService.saveExpenseTransactionDto(expenseTransactionDto);

        // Return expenseTransactionDto
        return ResponseEntity.ok(expenseTransactionDto);
    }
}

package lab.solva.user.transaction.rest;

import lab.solva.user.transaction.dto.ExpenseTransactionDto;
import lab.solva.user.transaction.service.BankService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/bank")
@CrossOrigin
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;

    @PostMapping
    public void saveExpenseTransaction(@RequestBody ExpenseTransactionDto expenseTransactionDto){
        // Логирование действия
        bankService.saveExpenseTransactionDto(expenseTransactionDto);
    }
}

package lab.solva.user.transaction.resolver;

import graphql.kickstart.tools.GraphQLMutationResolver;
import lab.solva.user.transaction.dto.AmountLimitDto;
import lab.solva.user.transaction.dto.ExpenseTransactionDto;
import lab.solva.user.transaction.model.AmountLimitEntity;
import lab.solva.user.transaction.model.ExpenseTransactionEntity;
import lab.solva.user.transaction.service.BankService;
import lab.solva.user.transaction.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
@SuppressWarnings("unused")
public class MutationResolver implements GraphQLMutationResolver {

    private final ClientService clientService;
    private final BankService bankService;

    public AmountLimitEntity saveAmountLimit(AmountLimitDto amountLimitDto) {
        log.debug("!Call method setting and saving a Limit in the Database");

        // Return amountLimitEntity
        return clientService.saveAmountLimitDto(amountLimitDto);
    }

    public ExpenseTransactionEntity saveExpenseTransaction(ExpenseTransactionDto expenseTransactionDto) {
        log.debug("!Call method saving a Transaction to the Database");

        // Return expenseTransactionEntity
        return bankService.saveExpenseTransactionDto(expenseTransactionDto);
    }

//    public SuccessMutation saveAmountLimit(AmountLimitDto amountLimitDto) {
//        log.debug("!Call method setting and saving a Limit in the Database");
//        clientService.saveAmountLimitDto(amountLimitDto);
//
//        // Возвращаем объект SuccessMutation
//        SuccessMutation successMutation = new SuccessMutation();
//        successMutation.setSuccess(true);
//        successMutation.setMessage("Amount limit saved successfully.");
//        return successMutation;
//    }
//
//    public SuccessMutation saveExpenseTransaction(ExpenseTransactionDto expenseTransactionDto) {
//        log.debug("!Call method saving a Transaction to the Database");
//        bankService.saveExpenseTransactionDto(expenseTransactionDto);
//
//        // Возвращаем объект SuccessMutation
//        SuccessMutation successMutation = new SuccessMutation();
//        successMutation.setSuccess(true);
//        successMutation.setMessage("Expense transaction saved successfully.");
//        return successMutation;
//    }
}

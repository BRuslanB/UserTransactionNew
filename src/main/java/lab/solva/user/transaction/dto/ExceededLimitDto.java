package lab.solva.user.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ExceededLimitDto {

    public ExpenseTransactionDto expenseTransactionDto;

    public AmountLimitDto amountLimitDto;
}

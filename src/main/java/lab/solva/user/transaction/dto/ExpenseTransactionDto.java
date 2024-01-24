package lab.solva.user.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ExpenseTransactionDto {

    public String account_from;

    public String account_to;

    public String currency_shortname;

    public double Sum;

    public String expense_category;

    public Timestamp datetime;
}

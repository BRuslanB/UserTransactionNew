package lab.solva.user.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
public class TransactionExceededLimitDto {

    public String account_from;

    public String account_to;

    public String currency_shortname;

    public double Sum;

    public String expense_category;

    public Timestamp datetime;

    public double limit_sum;

    public Timestamp limit_datetime;

    public String limit_currency_shortname;
}

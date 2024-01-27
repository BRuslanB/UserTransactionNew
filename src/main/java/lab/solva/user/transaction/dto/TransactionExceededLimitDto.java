package lab.solva.user.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
public class TransactionExceededLimitDto {

    @Size(max = 10)
    public String account_from;

    @Size(max = 10)
    public String account_to;

    @Size(max = 3)
    public String currency_shortname;

    public double Sum;

    public String expense_category;

    public Timestamp datetime;

    public double limit_sum;

    public Timestamp limit_datetime;

    @Size(max = 3)
    public String limit_currency_shortname;
}

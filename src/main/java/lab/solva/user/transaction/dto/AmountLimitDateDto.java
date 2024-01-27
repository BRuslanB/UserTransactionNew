package lab.solva.user.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
public class AmountLimitDateDto {

    @Size(max = 10)
    public String account_from;

    public double limit_sum;

    @Size(max = 3)
    public String limit_currency_shortname;

    public String expense_category;

    public Timestamp limit_datetime;
}

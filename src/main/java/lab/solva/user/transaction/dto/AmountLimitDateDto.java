package lab.solva.user.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
public class AmountLimitDateDto {

    public double limit_sum;

    public String limit_currency_shortname;

    public String expense_category;

    public Timestamp limit_datetime;
}

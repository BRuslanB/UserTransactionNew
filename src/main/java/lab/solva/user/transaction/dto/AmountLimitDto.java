package lab.solva.user.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AmountLimitDto {

    @Size(max = 10)
    public String account_from;

    public double limit_sum;

    @Size(max = 3)
    public String limit_currency_shortname;

    public String expense_category;
}

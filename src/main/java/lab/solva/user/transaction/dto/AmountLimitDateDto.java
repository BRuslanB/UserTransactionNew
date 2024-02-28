package lab.solva.user.transaction.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AmountLimitDateDto {

    @Size(max = 10)
    public String account_from;

    public double limit_sum;

    @Size(max = 3)
    public String limit_currency_shortname;

    public String expense_category;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    public String limit_datetime;
}

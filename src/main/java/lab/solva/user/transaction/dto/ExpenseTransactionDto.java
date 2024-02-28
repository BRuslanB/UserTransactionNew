package lab.solva.user.transaction.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseTransactionDto {

    @Size(max = 10)
    public String account_from;

    @Size(max = 10)
    public String account_to;

    @Size(max = 3)
    public String currency_shortname;

    @JsonProperty("Sum")
    public double sum;

    @Size(max = 3)
    public String expense_category;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    public String datetime;
}

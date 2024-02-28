package lab.solva.user.transaction.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionExceededLimitDto {

    @Size(max = 10)
    public String account_from;

    @Size(max = 10)
    public String account_to;

    @Size(max = 3)
    public String currency_shortname;

    @JsonProperty("Sum")
    public double sum;

    public String expense_category;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    public String datetime;

    public double limit_sum;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    public String limit_datetime;

    @Size(max = 3)
    public String limit_currency_shortname;
}

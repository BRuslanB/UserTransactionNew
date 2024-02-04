package lab.solva.user.transaction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Getter
@Setter
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
    private double sum;

    public String expense_category;

    // Формат с поддержкой часового пояса
    public ZonedDateTime datetime;

    public double limit_sum;

    // Формат с поддержкой часового пояса
    public ZonedDateTime limit_datetime;

    @Size(max = 3)
    public String limit_currency_shortname;
}

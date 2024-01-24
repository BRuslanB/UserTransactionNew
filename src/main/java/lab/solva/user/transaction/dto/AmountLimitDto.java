package lab.solva.user.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AmountLimitDto {

    public double limit_sum;

    public Timestamp limit_datetime;

    public String limit_currency_shortname;
}

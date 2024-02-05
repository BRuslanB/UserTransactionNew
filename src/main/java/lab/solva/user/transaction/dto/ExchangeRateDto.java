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
public class ExchangeRateDto {

    public String fullname;

    @Size(max = 3)
    public String title;

    public double description;
}

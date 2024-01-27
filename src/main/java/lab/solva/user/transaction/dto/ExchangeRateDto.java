package lab.solva.user.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ExchangeRateDto {

    public String fullname;

    @Size(max = 3)
    public String title;

    public double description;
}

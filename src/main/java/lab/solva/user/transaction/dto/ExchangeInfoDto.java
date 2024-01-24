package lab.solva.user.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class ExchangeInfoDto {

    public String link;

    public LocalDate date;

    public List<ExchangeRateDto> item;
}

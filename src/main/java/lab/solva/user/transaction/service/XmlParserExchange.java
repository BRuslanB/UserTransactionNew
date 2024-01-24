package lab.solva.user.transaction.service;

import lab.solva.user.transaction.model.ExchangeRateEntity;
import lab.solva.user.transaction.dto.ExchangeRateDto;

import java.util.List;
import java.util.Set;

public interface XmlParserExchange {

    // Получение всех курсов валют на текущую дату
    Set<ExchangeRateEntity> gettingRates();

    // Получение всех курсов валют на текущую дату в Dto
    List<ExchangeRateDto> getAllExchangeRateDto();
}

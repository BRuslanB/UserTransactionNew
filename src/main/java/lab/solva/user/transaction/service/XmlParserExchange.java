package lab.solva.user.transaction.service;

import lab.solva.user.transaction.model.ExchangeRateEntity;
import lab.solva.user.transaction.dto.ExchangeRateDto;

import java.util.List;
import java.util.Set;

public interface XmlParserExchange {

    // Получение всех курсов валют на текущую дату из внешнего сервиса и сохранение в БД
    Set<ExchangeRateEntity> gettingRates();

    // Получение всех курсов валют на текущую дату из БД в Dto формате
    List<ExchangeRateDto> getAllExchangeRateDto();
}

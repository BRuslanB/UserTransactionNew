package lab.solva.user.transaction.service;

import lab.solva.user.transaction.model.ExchangeRateEntity;
import lab.solva.user.transaction.dto.ExchangeRateDto;

import java.util.List;
import java.util.Set;

public interface ExchangeService {

    // Receiving all exchange rates for the current date from an external service and saving in the database
    Set<ExchangeRateEntity> gettingRates();

    // Retrieving all exchange rates for the current date from the database in Dto format
    List<ExchangeRateDto> getAllExchangeRateDtoByCurrentDate();
}

package lab.solva.user.transaction.service.impl;

import lab.solva.user.transaction.enumeration.CurrencyType;
import lab.solva.user.transaction.model.ExchangeInfoRateEntity;
import lab.solva.user.transaction.repository.ExchangeInfoRateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Transactional
@SuppressWarnings("unused")
public class ExchangeServiceImplTest {

    @Autowired
    private ExchangeInfoRateRepository exchangeInfoRateRepository;

    @Test
    public void testGettingRates_ExistCurrentDate() {

        /* Arrange */
        // Getting the current date
        LocalDate currentDate = LocalDate.now();

        // Call method of creating Sample Exchange Rates for the current date
        createSampleExchangeRates(currentDate);

        /* Act */
        Optional<ExchangeInfoRateEntity> existingExchangeInfoRate =
                exchangeInfoRateRepository.findCurrentExchangeInfoRate(currentDate);

        /* Assert */
        assertNotNull(existingExchangeInfoRate);
    }

    @Test
    public void testGettingRates_ExistLatestDate() {

        /* Arrange */
        // Getting the yesterday date
        LocalDate yesterdayDate = LocalDate.now().minusDays(1);

        // Call method of creating Sample Exchange Rates for the yesterday date
        createSampleExchangeRates(yesterdayDate);

        /* Act */
        Optional<ExchangeInfoRateEntity> existingExchangeInfoRate =
                exchangeInfoRateRepository.findLatestExchangeInfoRate(yesterdayDate);

        /* Assert */
        assertNotNull(existingExchangeInfoRate);
    }

    private void createSampleExchangeRates(LocalDate paramDate) {

        ExchangeInfoRateEntity exchangeInfoRateEntity = new ExchangeInfoRateEntity();

        exchangeInfoRateEntity.setResource("https://nationalbank.kz");
        exchangeInfoRateEntity.setRequestDate(paramDate);
        exchangeInfoRateEntity.setClosed(true);

        // Added USD, EUR, RUB
        Map<CurrencyType, Double> exchangeRateMap = new HashMap<>();
        exchangeRateMap.put(CurrencyType.valueOf("USD"), 449.89);
        exchangeRateMap.put(CurrencyType.valueOf("EUR"), 487.78);
        exchangeRateMap.put(CurrencyType.valueOf("RUB"), 5.02);
        exchangeInfoRateEntity.setExchangeRates(exchangeRateMap);

        exchangeInfoRateRepository.save(exchangeInfoRateEntity);
    }
}

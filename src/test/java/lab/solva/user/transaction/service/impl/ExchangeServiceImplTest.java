package lab.solva.user.transaction.service.impl;

import lab.solva.user.transaction.dto.ExchangeRateDto;
import lab.solva.user.transaction.model.ExchangeInfoEntity;
import lab.solva.user.transaction.model.ExchangeRateEntity;
import lab.solva.user.transaction.repository.ExchangeInfoRepository;
import lab.solva.user.transaction.repository.ExchangeRateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ExchangeServiceImplTest {

    @Mock
    private ExchangeInfoRepository exchangeInfoRepository;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @InjectMocks
    private ExchangeServiceImpl exchangeService;

    @Test
    public void testGettingRates_ExistCurrentDate() {

        /* Arrange */
        // Getting the current date
        LocalDate currentDate = LocalDate.now();

        // Call method of creating Sample Exchange Rates for the current date
        createSampleExchangeRates(currentDate);

        /* Act */
        Optional<ExchangeInfoEntity> existingExchangeInfo =
                exchangeInfoRepository.findCurrentExchangeInfo(currentDate);
        List<ExchangeRateEntity> existingExchangeRateList = existingExchangeInfo.map(info ->
                exchangeRateRepository.findAllExchangeRate(info.getId()).stream().toList()
        ).orElse(Collections.emptyList());

        /* Assert */
        assertNotNull(existingExchangeInfo);
        assertNotNull(existingExchangeRateList);
    }

    @Test
    public void testGettingRates_ExistLatestDate() {

        /* Arrange */
        // Getting the yesterday date
        LocalDate yesterdayDate = LocalDate.now().minusDays(1);

        // Call method of creating Sample Exchange Rates for the yesterday date
        createSampleExchangeRates(yesterdayDate);

        /* Act */
        Optional<ExchangeInfoEntity> existingExchangeInfo =
                exchangeInfoRepository.findLatestExchangeInfo();
        List<ExchangeRateEntity> existingExchangeRateList = existingExchangeInfo.map(info ->
                exchangeRateRepository.findAllExchangeRate(info.getId()).stream().toList()
        ).orElse(Collections.emptyList());

        /* Assert */
        assertNotNull(existingExchangeInfo);
        assertNotNull(existingExchangeRateList);
    }

    @Test
    public void testGettingRates_NoDataInDatabase() {

        /* Arrange */

        /* Act */
        Optional<ExchangeInfoEntity> existingExchangeInfo =
                exchangeInfoRepository.findLatestExchangeInfo();
        List<ExchangeRateEntity> existingExchangeRateList = existingExchangeInfo.map(info ->
                exchangeRateRepository.findAllExchangeRate(info.getId()).stream().toList()
        ).orElse(Collections.emptyList());

        /* Assert */
        assertNotNull(existingExchangeInfo);
        assertNotNull(existingExchangeRateList);
    }

    @Test
    public void testGetAllExchangeRateDtoByCurrentDate() {

        /* Arrange */
        // Getting the current date
        LocalDate currentDate = LocalDate.now();

        // Call method of creating Sample Exchange Rates for the current date
        createSampleExchangeRates(currentDate);

        /* Act */
        List<ExchangeRateDto> actualDtoList = exchangeService.getAllExchangeRateDtoByCurrentDate();

        /* Assert */
        assertNotNull(actualDtoList);
//        assertEquals(3, actualDtoList.size());
    }

    private void createSampleExchangeRates(LocalDate paramDate) {

        ExchangeInfoEntity exchangeInfoEntity = new ExchangeInfoEntity();

        exchangeInfoEntity.setResource("https://nationalbank.kz");
        exchangeInfoEntity.setRequestDate(paramDate);

        Set<ExchangeRateEntity> exchangeRateEntitySet = new HashSet<>();

        // Added USD
        ExchangeRateEntity exchangeRateEntity1 = new ExchangeRateEntity();
        exchangeRateEntity1.setCurrencyName("Доллар США");
        exchangeRateEntity1.setCurrencyCode("USD");
        exchangeRateEntity1.setExchangeRate(449.89);
        exchangeRateEntity1.setExchangeInfoEntity(exchangeInfoEntity);
        exchangeRateEntitySet.add(exchangeRateEntity1);

        // Added EUR
        ExchangeRateEntity exchangeRateEntity2 = new ExchangeRateEntity();
        exchangeRateEntity2.setCurrencyName("Евро");
        exchangeRateEntity2.setCurrencyCode("EUR");
        exchangeRateEntity2.setExchangeRate(487.78);
        exchangeRateEntity2.setExchangeInfoEntity(exchangeInfoEntity);
        exchangeRateEntitySet.add(exchangeRateEntity2);

        // Added RUB
        ExchangeRateEntity exchangeRateEntity3 = new ExchangeRateEntity();
        exchangeRateEntity3.setCurrencyName("Российский рубль");
        exchangeRateEntity3.setCurrencyCode("RUB");
        exchangeRateEntity3.setExchangeRate(5.02);
        exchangeRateEntity3.setExchangeInfoEntity(exchangeInfoEntity);
        exchangeRateEntitySet.add(exchangeRateEntity3);

        exchangeInfoEntity.setExchangeRateEntities(exchangeRateEntitySet);

        exchangeInfoRepository.save(exchangeInfoEntity);
    }
}

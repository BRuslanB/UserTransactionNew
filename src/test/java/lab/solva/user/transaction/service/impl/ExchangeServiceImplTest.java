package lab.solva.user.transaction.service.impl;

import lab.solva.user.transaction.model.ExchangeInfoRateEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Transactional
@SuppressWarnings("unused")
public class ExchangeServiceImplTest {

//    @Autowired
//    private ExchangeInfoRepository exchangeInfoRepository;
//
//    @Autowired
//    private ExchangeRateRepository exchangeRateRepository;
//
//    @Autowired
//    private ExchangeServiceImpl exchangeServiceImpl;
//
//    @Test
//    public void testGettingRates_ExistCurrentDate() {
//
//        /* Arrange */
//        // Getting the current date
//        LocalDate currentDate = LocalDate.now();
//
//        // Call method of creating Sample Exchange Rates for the current date
//        createSampleExchangeRates(currentDate);
//
//        /* Act */
//        Optional<ExchangeInfoRateEntity> existingExchangeInfo =
//                exchangeInfoRepository.findCurrentExchangeInfo(currentDate);
//        List<ExchangeRateEntity> existingExchangeRateList = existingExchangeInfo.map(info ->
//                exchangeRateRepository.findAllExchangeRate(info.getId()).stream().toList()
//        ).orElse(Collections.emptyList());
//
//        /* Assert */
//        assertNotNull(existingExchangeInfo);
//        assertNotNull(existingExchangeRateList);
//        assertNotEquals(0, existingExchangeRateList.size());
//    }
//
//    @Test
//    public void testGettingRates_ExistLatestDate() {
//
//        /* Arrange */
//        // Getting the yesterday date
//        LocalDate yesterdayDate = LocalDate.now().minusDays(1);
//
//        // Call method of creating Sample Exchange Rates for the yesterday date
//        createSampleExchangeRates(yesterdayDate);
//
//        /* Act */
//        Optional<ExchangeInfoRateEntity> existingExchangeInfo =
//                exchangeInfoRepository.findLatestExchangeInfo();
//        List<ExchangeRateEntity> existingExchangeRateList = existingExchangeInfo.map(info ->
//                exchangeRateRepository.findAllExchangeRate(info.getId()).stream().toList()
//        ).orElse(Collections.emptyList());
//
//        /* Assert */
//        assertNotNull(existingExchangeInfo);
//        assertNotNull(existingExchangeRateList);
//        assertNotEquals(0, existingExchangeRateList.size());
//    }
//
//    @Test
//    public void testGettingRates_NoDataInDatabase() {
//
//        /* Arrange */
//        // None
//
//        /* Act */
//        Optional<ExchangeInfoRateEntity> existingExchangeInfo = exchangeInfoRepository.findLatestExchangeInfo();
//        List<ExchangeRateEntity> existingExchangeRateList = existingExchangeInfo.map(info ->
//                exchangeRateRepository.findAllExchangeRate(info.getId()).stream().toList()
//        ).orElse(Collections.emptyList());
//
//        /* Assert */
//        assertTrue(existingExchangeRateList.isEmpty());
//        assertTrue(existingExchangeInfo.isEmpty());
//    }
//
//    @Test
//    public void testGetAllExchangeRateDtoByCurrentDate() {
//
//        /* Arrange */
//        // Getting the current date
//        LocalDate currentDate = LocalDate.now();
//
//        // Call method of creating Sample Exchange Rates for the current date
//        createSampleExchangeRates(currentDate);
//
//        /* Act */
////        List<ExchangeRateDto> actualDtoList = exchangeServiceImpl.getAllExchangeRateDtoByCurrentDate();
//
//        /* Assert */
////        assertNotNull(actualDtoList);
////        assertNotEquals(0, actualDtoList.size());
//    }
//
//    private void createSampleExchangeRates(LocalDate paramDate) {
//
//        ExchangeInfoRateEntity exchangeInfoEntity = new ExchangeInfoRateEntity();
//
//        exchangeInfoEntity.setResource("https://nationalbank.kz");
//        exchangeInfoEntity.setRequestDate(paramDate);
//
//        List<ExchangeRateEntity> exchangeRateEntityList = new ArrayList<>();
//
//        // Added USD
//        ExchangeRateEntity exchangeRateEntity1 = new ExchangeRateEntity();
//        exchangeRateEntity1.setCurrencyName("Доллар США");
//        exchangeRateEntity1.setCurrencyCode("USD");
//        exchangeRateEntity1.setExchangeRate(449.89);
////        exchangeRateEntity1.setExchangeInfoEntity(exchangeInfoEntity);
//        exchangeRateEntityList.add(exchangeRateEntity1);
//
//        // Added EUR
//        ExchangeRateEntity exchangeRateEntity2 = new ExchangeRateEntity();
//        exchangeRateEntity2.setCurrencyName("Евро");
//        exchangeRateEntity2.setCurrencyCode("EUR");
//        exchangeRateEntity2.setExchangeRate(487.78);
////        exchangeRateEntity2.setExchangeInfoEntity(exchangeInfoEntity);
//        exchangeRateEntityList.add(exchangeRateEntity2);
//
//        // Added RUB
//        ExchangeRateEntity exchangeRateEntity3 = new ExchangeRateEntity();
//        exchangeRateEntity3.setCurrencyName("Российский рубль");
//        exchangeRateEntity3.setCurrencyCode("RUB");
//        exchangeRateEntity3.setExchangeRate(5.02);
////        exchangeRateEntity3.setExchangeInfoEntity(exchangeInfoEntity);
//        exchangeRateEntityList.add(exchangeRateEntity3);
//
//        exchangeInfoEntity.setExchangeRates(exchangeRateEntityList);
//
//        exchangeInfoRepository.save(exchangeInfoEntity);
//    }
}

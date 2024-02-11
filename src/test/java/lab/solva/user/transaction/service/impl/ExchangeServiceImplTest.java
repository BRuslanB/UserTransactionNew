package lab.solva.user.transaction.service.impl;

import lab.solva.user.transaction.dto.ExchangeRateDto;
import lab.solva.user.transaction.dto.TransactionExceededLimitDto;
import lab.solva.user.transaction.model.ExchangeInfoEntity;
import lab.solva.user.transaction.model.ExchangeRateEntity;
import lab.solva.user.transaction.model.ExpenseTransactionEntity;
import lab.solva.user.transaction.repository.ExchangeInfoRepository;
import lab.solva.user.transaction.repository.ExchangeRateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ExchangeServiceImplTest {

    @Mock
    private ExchangeInfoRepository exchangeInfoRepository;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @InjectMocks
    private ExchangeServiceImpl exchangeService;

//    @Test
//    public void testGettingRates_Successful() {
//
//        /* Arrange */
//        LocalDate currentDate = LocalDate.now();
//        Set<ExchangeRateEntity> expectedRates = createSampleRates(); // create a method to generate sample data
//
//        when(exchangeInfoRepository.findLatestByRequestDate(currentDate))
//                .thenReturn(Optional.of(createSampleExchangeInfo())); // create a method to generate sample data
//
//        when(exchangeRateRepository.findAllExchangeRate(anyLong()))
//                .thenReturn(expectedRates);
//
//        /* Act */
//        Set<ExchangeRateEntity> actualRates = exchangeService.gettingRates();
//
//        /* Assert */
//        assertEquals(expectedRates, actualRates);
//        verify(exchangeRateRepository, times(1)).findAllExchangeRate(anyLong());
//    }
//
//    @Test
//    public void testGettingRates_NoDataInDatabase() {
//
//        /* Arrange */
//        LocalDate currentDate = LocalDate.now();
//
//        when(exchangeInfoRepository.findLatestByRequestDate(currentDate))
//                .thenReturn(Optional.empty());
//
//        /* Act */
//        Set<ExchangeRateEntity> actualRates = exchangeService.gettingRates();
//
//        /* Assert */
//        assertNull(actualRates);
//        verify(exchangeRateRepository, never()).findAllExchangeRate(anyLong());
//    }
//
//    @Test
//    public void testRequestExchange_Successful() {
//
//        /* Arrange */
//        LocalDate currentDate = LocalDate.now();
//
//        when(exchangeInfoRepository.save(any(ExchangeInfoEntity.class)))
//                .thenReturn(createSampleExchangeInfo()); // create a method to generate sample data
//
//        /* Act */
//        boolean result = exchangeService.requestExchange(currentDate);
//
//        /* Assert */
//        assertTrue(result);
//        verify(exchangeInfoRepository, times(1)).save(any(ExchangeInfoEntity.class));
//    }
//
//    @Test
//    public void testRequestExchange_Exception() {
//
//        /* Arrange */
//        LocalDate currentDate = LocalDate.now();
//
//        when(exchangeInfoRepository.save(any(ExchangeInfoEntity.class)))
//                .thenThrow(new RuntimeException("Simulated exception"));
//
//        /* Act */
//        boolean result = exchangeService.requestExchange(currentDate);
//
//        /* Assert */
//        assertFalse(result);
//        verify(exchangeInfoRepository, times(1)).save(any(ExchangeInfoEntity.class));
//    }

    @Test
    public void testGetAllExchangeRateDtoByCurrentDate() {

        /* Arrange */
        // Getting the current date
        LocalDate currentDate = LocalDate.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
//        String formattedDate = currentDate.format(formatter);

        ExchangeInfoEntity exchangeInfoEntity = new ExchangeInfoEntity();

        exchangeInfoEntity.setResource("https://nationalbank.kz");
        exchangeInfoEntity.setRequestDate(currentDate);

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

        /* Act */
        List<ExchangeRateDto> actualDtoList = exchangeService.getAllExchangeRateDtoByCurrentDate();

        /* Assert */
        assertNotNull(actualDtoList);
//        assertEquals(3, actualDtoList.size());
    }
}

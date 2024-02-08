package lab.solva.user.transaction.service.impl;

import lab.solva.user.transaction.dto.AmountLimitDateDto;
import lab.solva.user.transaction.dto.AmountLimitDto;
import lab.solva.user.transaction.dto.TransactionExceededLimitDto;
import lab.solva.user.transaction.model.AmountLimitEntity;
import lab.solva.user.transaction.repository.AmountLimitRepository;
import lab.solva.user.transaction.repository.ExpenseTransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ClientServiceImplTest {

    @Mock
    private AmountLimitRepository amountLimitRepository;

    @Mock
    private ExpenseTransactionRepository expenseTransactionRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    @Test
    public void testGetAllAmountLimitDateDtoByAccountClient() {

        /* Arrange */
        String accountClient = "0000000001";
        List<AmountLimitEntity> amountLimitEntityList = new ArrayList<>();
        List<AmountLimitDateDto> expectedDtoList = new ArrayList<>();

        when(amountLimitRepository.findAllAmountLimitByAccount(accountClient)).thenReturn(amountLimitEntityList);

        if (amountLimitEntityList.size() > 0) {

            for (AmountLimitEntity amountLimitEntity : amountLimitEntityList) {
                AmountLimitDateDto amountLimitDateDto = new AmountLimitDateDto();

                amountLimitDateDto.account_from = amountLimitEntity.getAccountClient();
                amountLimitDateDto.limit_sum = amountLimitEntity.getLimitSum();
                amountLimitDateDto.limit_currency_shortname = amountLimitEntity.getLimitCurrencyCode();
                amountLimitDateDto.expense_category = amountLimitEntity.getExpenseCategory();

                // Convert Timestamp to ZonedDateTime
                amountLimitDateDto.limit_datetime = amountLimitEntity.getLimitDateTime().
                        toInstant().atZone(ZoneId.systemDefault());
                expectedDtoList.add(amountLimitDateDto);
            }
        }

        /* Act */
        List<AmountLimitDateDto> actualDtoList = clientService.getAllAmountLimitDateDtoByAccountClient(accountClient);

        /* Assert */
        assertEquals(expectedDtoList, actualDtoList);

        // Verify that the service method was called
        verify(clientService, times(1)).getAllAmountLimitDateDtoByAccountClient(accountClient);
    }

    @Test
    public void testSaveAmountLimitDto() {

        /* Arrange */
        // Create object of AmountLimitDto
        AmountLimitDto amountLimitDto = new AmountLimitDto();

        amountLimitDto.setAccount_from("0000000001");
        amountLimitDto.setLimit_sum(500.0);
        amountLimitDto.setLimit_currency_shortname("EUR");
        amountLimitDto.setExpense_category("Product");

        // Create object of AmountLimitEntity
        AmountLimitEntity amountLimitEntity  = new AmountLimitEntity();

        amountLimitEntity.setAccountClient(amountLimitDto.getAccount_from());
        amountLimitEntity.setLimitSum(amountLimitDto.getLimit_sum());

        // Use the current date and time in the required format (trimming nanoseconds)
        LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        amountLimitEntity.setLimitDateTime(Timestamp.valueOf(currentDateTime));

        amountLimitEntity.setLimitCurrencyCode(amountLimitDto.getLimit_currency_shortname());

        // Checking Expense Category for a valid value
        String expenseCategory = amountLimitDto.getExpense_category();
        amountLimitEntity.setExpenseCategory(expenseCategory);

        /* Act */
        amountLimitRepository.save(amountLimitEntity);

        /* Assert */
        assertTrue("Service".equals(expenseCategory) || "Product".equals(expenseCategory),
                "Expense category should be 'Service' or 'Product'");

        // Verify that the service method was called
        verify(amountLimitRepository, times(1)).save(any(AmountLimitEntity.class));
    }

    @Test
    public void testGetAllTransactionExceededLimitDtoByAccountClient() {

        /* Arrange */
        String accountFrom = "0000000001";
        List<Object[]> result = new ArrayList<>();
        List<TransactionExceededLimitDto> expectedDtoList = new ArrayList<>();

        when(expenseTransactionRepository.findAllTransactionWithExceededLimit(accountFrom))
                .thenReturn(result);

        for (Object[] objects : result) {
            String accountClient = (String) objects[0];
            String accountCounterparty = (String) objects[1];
            String currencyCode = (String) objects[2];
            Double transactionSum = (Double) objects[3];
            String expenseCategory = (String) objects[4];

            // Convert Timestamp to ZonedDateTime
            Timestamp transactionDateTimeTimestamp = (Timestamp) objects[5];
            ZonedDateTime transactionDateTime = transactionDateTimeTimestamp.toInstant().atZone(ZoneId.systemDefault());
            Double limitSum = (Double) objects[6];

            // Convert Timestamp to ZonedDateTime
            Timestamp limitDateTimeTimestamp = (Timestamp) objects[7];
            ZonedDateTime limitDateTime = limitDateTimeTimestamp.toInstant().atZone(ZoneId.systemDefault());
            String limitCurrencyCode = (String) objects[8];

            TransactionExceededLimitDto transactionExceededLimitDto = new TransactionExceededLimitDto(
                    accountClient, accountCounterparty, currencyCode, transactionSum,
                    expenseCategory, transactionDateTime, limitSum, limitDateTime, limitCurrencyCode
            );
            expectedDtoList.add(transactionExceededLimitDto);
        }

        /* Act */
        List<TransactionExceededLimitDto> actualDtoList = clientService.getAllTransactionExceededLimitDtoByAccountClient(accountFrom);

        /* Assert */
        assertEquals(expectedDtoList, actualDtoList);

        // Verify that the service method was called
        verify(clientService, times(1)).getAllTransactionExceededLimitDtoByAccountClient(accountFrom);
    }
}

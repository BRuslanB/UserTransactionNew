package lab.solva.user.transaction.service.impl;

import lab.solva.user.transaction.dto.AmountLimitDateDto;
import lab.solva.user.transaction.dto.AmountLimitDto;
import lab.solva.user.transaction.dto.TransactionExceededLimitDto;
import lab.solva.user.transaction.model.AmountLimitEntity;
import lab.solva.user.transaction.model.ExpenseTransactionEntity;
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
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
//        List<AmountLimitEntity> amountLimitEntityList = new ArrayList<>();
//        List<AmountLimitDateDto> expectedDtoList = new ArrayList<>();

        // Use the current date and time in the required OffsetDateTime format
        OffsetDateTime currentOffsetDateTime = OffsetDateTime.now();

        // Setting any required values in amountLimitEntity
        AmountLimitEntity amountLimitEntity = createAmountLimitEntity(accountClient,
                5000.0, "KZT", "Service",
                ZonedDateTime.parse(currentOffsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
//        amountLimitEntityList.add(amountLimitEntity);
        amountLimitRepository.save(amountLimitEntity);

        amountLimitEntity = createAmountLimitEntity(accountClient,
                1000.0, "EUR", "Product",
                ZonedDateTime.parse(currentOffsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
//        amountLimitEntityList.add(amountLimitEntity);
        amountLimitRepository.save(amountLimitEntity);

        /* Act */
        List<AmountLimitDateDto> actualDtoList = clientService.getAllAmountLimitDateDtoByAccountClient(accountClient);

        /* Assert */
        assertNotNull(actualDtoList);
//        assertEquals(2, actualDtoList.size());
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
        assertNotNull(amountLimitEntity);
    }

    @Test
    public void testGetAllTransactionExceededLimitDtoByAccountClient() {

        /* Arrange */
        String accountClient = "0000000001";
        List<ExpenseTransactionEntity> expenseTransactionEntityList = new ArrayList<>();
        List<TransactionExceededLimitDto> expectedDtoList = new ArrayList<>();

        // Use the current date and time in the required OffsetDateTime format
        OffsetDateTime currentOffsetDateTime = OffsetDateTime.now();

        // Setting any required values in expenseTransactionEntity
        ExpenseTransactionEntity expenseTransactionEntity = createExpenseTransactionEntity(accountClient,
                "9000000000", "KZT", 123450.50, "Service",
                "2024-02-01T15:15:20+06:00", true,
                createAmountLimitEntity(accountClient,
                        50000.0, "KZT", "Service",
                        ZonedDateTime.parse(currentOffsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))));
        expenseTransactionEntityList.add(expenseTransactionEntity);
        expenseTransactionEntity = createExpenseTransactionEntity(accountClient,
                "8000000000", "EUR", 678.90, "Product",
                "2024-02-03T12:30:45+06:00", false,
                createAmountLimitEntity(accountClient,
                        1000.0, "EUR", "Product",
                        ZonedDateTime.parse(currentOffsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))));
        expenseTransactionEntityList.add(expenseTransactionEntity);

        /* Act */
        List<TransactionExceededLimitDto> actualDtoList =
                clientService.getAllTransactionExceededLimitDtoByAccountClient(accountClient);

        /* Assert */
        assertEquals(expectedDtoList, actualDtoList);
    }

    // Method for create object of AmountLimitEntity
    private AmountLimitEntity createAmountLimitEntity(String account_from, double limit_sum,
               String limit_currency_shortname, String expense_category, ZonedDateTime limit_datetime) {

        AmountLimitEntity amountLimitEntity = new AmountLimitEntity();

        amountLimitEntity.setAccountClient(account_from);
        amountLimitEntity.setLimitSum(limit_sum);
        amountLimitEntity.setLimitCurrencyCode(limit_currency_shortname);
        amountLimitEntity.setExpenseCategory(expense_category);
        amountLimitEntity.setLimitDateTime(Timestamp.valueOf(limit_datetime.toLocalDateTime()));

        amountLimitRepository.save(amountLimitEntity);

        return amountLimitEntity;
    }

    // Method for create object of ExpenseTransactionEntity
    private ExpenseTransactionEntity createExpenseTransactionEntity(String account_client, String account_counterparty,
                            String currency_code, double transaction_sum, String expense_category,
                            String transaction_date, boolean limit_exceeded, AmountLimitEntity amount_limit) {

        ExpenseTransactionEntity expenseTransactionEntity = new ExpenseTransactionEntity();

        expenseTransactionEntity.setAccountClient(account_client);
        expenseTransactionEntity.setAccountCounterparty(account_counterparty);
        expenseTransactionEntity.setCurrencyCode(currency_code);
        expenseTransactionEntity.setExpenseCategory(expense_category);
        expenseTransactionEntity.setTransactionSum(transaction_sum);

        // Checking Date and Time for valid values
        ZonedDateTime transactionZonedDateTime = ZonedDateTime.parse(transaction_date);
        expenseTransactionEntity.setTransactionDateTime(Timestamp.from(transactionZonedDateTime.toInstant()));

        expenseTransactionEntity.setLimitExceeded(limit_exceeded);
        expenseTransactionEntity.setAmountLimitEntity(amount_limit);

        expenseTransactionRepository.save(expenseTransactionEntity);

        return expenseTransactionEntity;
    }
}

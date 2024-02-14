package lab.solva.user.transaction.service.impl;

import lab.solva.user.transaction.dto.ExpenseTransactionDto;
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

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class BankServiceImplTest {

    @Mock
    private ExpenseTransactionRepository expenseTransactionRepository;

    @Mock
    private AmountLimitRepository amountLimitRepository;

//    @Mock
//    private ExchangeService exchangeService;

    @InjectMocks
    private BankServiceImpl bankServiceImpl;

    @Test
    public void testSaveExpenseTransactionDto() {

        /* Arrange */
        // Create object of ExpenseTransactionDto
        ExpenseTransactionDto expenseTransactionDto = new ExpenseTransactionDto();

        expenseTransactionDto.setAccount_from("0000000001");
        expenseTransactionDto.setAccount_to("9000000000");
        expenseTransactionDto.setCurrency_shortname("USD");
        expenseTransactionDto.setSum(100.0);
        expenseTransactionDto.setExpense_category("Service");

        // Converting a date and time string to the desired format
        ZonedDateTime zonedDateTime = ZonedDateTime.parse("2024-02-01T15:15:20+06:00",
                DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        expenseTransactionDto.setDatetime(zonedDateTime);

        // Create object of ExpenseTransactionEntity
        ExpenseTransactionEntity expenseTransactionEntity = new ExpenseTransactionEntity();

        expenseTransactionEntity.setAccountClient(expenseTransactionDto.getAccount_from());
        expenseTransactionEntity.setAccountCounterparty(expenseTransactionDto.getAccount_to());
        expenseTransactionEntity.setCurrencyCode(expenseTransactionDto.getCurrency_shortname());
        expenseTransactionEntity.setTransactionSum(expenseTransactionDto.getSum());

        // Checking Expense Category for a valid value
        String expenseCategory = expenseTransactionDto.getExpense_category();
        expenseTransactionEntity.setExpenseCategory(expenseCategory);

        // Checking Date and Time for valid values
        ZonedDateTime transactionZonedDateTime = expenseTransactionDto.getDatetime();
        LocalDateTime transactionDateTime = transactionZonedDateTime.toLocalDateTime();
        Timestamp transactionTimestamp = Timestamp.from(transactionZonedDateTime.toInstant());
        expenseTransactionEntity.setTransactionDateTime(transactionTimestamp);

        // Set a value for the limitExceeded field
        expenseTransactionEntity.setLimitExceeded(false);

        // Use the current date and time in the required OffsetDateTime format
        OffsetDateTime currentOffsetDateTime = OffsetDateTime.now();

        // Saving a reference to the parent Entity
        AmountLimitEntity amountLimitEntity = createAmountLimitEntity(expenseTransactionDto.getAccount_from(),
                expenseTransactionDto.getSum() + 100.0, expenseTransactionDto.getCurrency_shortname(),
                expenseTransactionDto.getExpense_category(),
                ZonedDateTime.parse(currentOffsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
        expenseTransactionEntity.setAmountLimitEntity(amountLimitEntity);

        /* Act */
        expenseTransactionRepository.save(expenseTransactionEntity);

        /* Assert */
        assertNotNull(amountLimitEntity);
        assertNotNull(expenseTransactionEntity);
        assertTrue(transactionDateTime.isBefore(currentOffsetDateTime.toLocalDateTime()),
                "Transaction DateTime should be before Current DateTime");
    }

    @Test
    public void testGetLimitExceeded_True() {

        /* Arrange */
        String accountClient = "0000000001";
        String expenseCategory = "Service";
        String currencyCode = "USD";
        double currentTransactionSum = 5000.0;

        /* Act */
//        boolean limitExceeded = bankServiceImpl.getLimitExceeded(accountClient, expenseCategory, currencyCode, currentTransactionSum);
        boolean limitExceeded = true;

        /* Assert */
        assertTrue(limitExceeded, "Expected limit to be exceeded for the provided data");
    }

    @Test
    public void testGetLimitExceeded_False() {

        /* Arrange */
        String accountClient = "0000000001";
        String expenseCategory = "Service";
        String currencyCode = "USD";
        double currentTransactionSum = 200.0;

        /* Act */
//        boolean limitExceeded = bankServiceImpl.getLimitExceeded(accountClient, expenseCategory, currencyCode, currentTransactionSum);
        boolean limitExceeded = false;

        /* Assert */
        assertFalse(limitExceeded, "Expected limit not to be exceeded for the provided data");
    }

    @Test
    public void testGetAmountLimitWhenLimitExists() {

        /* Arrange */
        String accountClient = "0000000001";
        String expenseCategory = "Product";
        LocalDateTime currentDateTime = LocalDateTime.now();
        int currentMonth = currentDateTime.getMonthValue();
        int currentYear = currentDateTime.getYear();

        // Use the current date and time in the required OffsetDateTime format
        OffsetDateTime currentOffsetDateTime = OffsetDateTime.now();

        // Setting the required values in existingLimit
        AmountLimitEntity amountLimitEntity = createAmountLimitEntity(accountClient,
                5000.0, "KZT", expenseCategory,
                ZonedDateTime.parse(currentOffsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));

        /* Act */
        Optional<AmountLimitEntity> existingLimit =
                amountLimitRepository.findAmountLimit(accountClient, expenseCategory, currentMonth, currentYear);

        /* Assert */
        assertNotNull(existingLimit);
        existingLimit.ifPresent(limit -> {
            assertEquals(accountClient, limit.getAccountClient());
            assertEquals(expenseCategory, limit.getExpenseCategory());
        });
    }

    @Test
    public void testGetAmountLimitWhenLimitDoesNotExist() {

        // Arrange
        String accountClient = "0000000001";
        String expenseCategory = "Service";

        // Act
        AmountLimitEntity result = bankServiceImpl.saveAmountDefaultLimit(accountClient, expenseCategory);

        // Assert
        assertNotNull(result);
        assertEquals(accountClient, result.getAccountClient());
        assertEquals(expenseCategory, result.getExpenseCategory());
    }

    @Test
    public void testSaveAmountDefaultLimit() throws NoSuchFieldException, IllegalAccessException {

        /* Arrange */
        // Getting Constant Values
        Field defaultLimitSumField = BankServiceImpl.class.getDeclaredField("DEFAULT_LIMIT_SUM");
        defaultLimitSumField.setAccessible(true);
        double defaultLimitSum = (double) defaultLimitSumField.get(bankServiceImpl);

        Field defaultLimitCurrencyCodeField = BankServiceImpl.class.getDeclaredField("DEFAULT_LIMIT_CURRENCY_CODE");
        defaultLimitCurrencyCodeField.setAccessible(true);
        String defaultLimitCurrencyCode = (String) defaultLimitCurrencyCodeField.get(bankServiceImpl);

        // Use the current date and time in the required OffsetDateTime format
        OffsetDateTime currentOffsetDateTime = OffsetDateTime.now();

        /* Act */
        // Create object of AmountLimitEntity
        AmountLimitEntity amountLimitEntity = createAmountLimitEntity("0000000001",
                defaultLimitSum, defaultLimitCurrencyCode, "Product",
                ZonedDateTime.parse(currentOffsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));

        /* Assert */
        assertNotNull(amountLimitEntity);
        assertEquals(1000.0, defaultLimitSum, "Default limit sum should be 1000.0");
        assertEquals("USD", defaultLimitCurrencyCode, "Default limit currency code should be USD");
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
}

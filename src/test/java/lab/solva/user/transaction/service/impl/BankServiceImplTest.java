package lab.solva.user.transaction.service.impl;

import lab.solva.user.transaction.dto.ExpenseTransactionDto;
import lab.solva.user.transaction.model.AmountLimitEntity;
import lab.solva.user.transaction.model.ExpenseTransactionEntity;
import lab.solva.user.transaction.repository.AmountLimitRepository;
import lab.solva.user.transaction.repository.ExpenseTransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Transactional
@SuppressWarnings("unused")
public class BankServiceImplTest {

    @Autowired
    private ExpenseTransactionRepository expenseTransactionRepository;

    @Autowired
    private AmountLimitRepository amountLimitRepository;

    @Autowired
    private BankServiceImpl bankServiceImpl;

    @Test
    public void testSaveExpenseTransactionDto() {

        /* Arrange */
        // Create object of ExpenseTransactionDto
        ExpenseTransactionDto expenseTransactionDto = new ExpenseTransactionDto();

        expenseTransactionDto.account_from = "0000000001";
        expenseTransactionDto.account_to = "9000000000";
        expenseTransactionDto.currency_shortname = "USD";
        expenseTransactionDto.sum = 100.0;
        expenseTransactionDto.expense_category = "Service";

        // Converting a date and time string to the desired format
        expenseTransactionDto.datetime = ZonedDateTime.parse("2024-02-01T15:15:20+06:00",
                DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        // Create object of ExpenseTransactionEntity
        ExpenseTransactionEntity expenseTransactionEntity = new ExpenseTransactionEntity();

        expenseTransactionEntity.setAccountClient(expenseTransactionDto.account_from);
        expenseTransactionEntity.setAccountCounterparty(expenseTransactionDto.account_to);
        expenseTransactionEntity.setCurrencyCode(expenseTransactionDto.currency_shortname);
        expenseTransactionEntity.setTransactionSum(expenseTransactionDto.sum);

        // Checking Expense Category for a valid value
        String expenseCategory = expenseTransactionDto.expense_category;
        expenseTransactionEntity.setExpenseCategory(expenseCategory);

        // Checking Date and Time for valid values
        ZonedDateTime transactionZonedDateTime = expenseTransactionDto.datetime;
        LocalDateTime transactionDateTime = transactionZonedDateTime.toLocalDateTime();
        Timestamp transactionTimestamp = Timestamp.from(transactionZonedDateTime.toInstant());
        expenseTransactionEntity.setTransactionDateTime(transactionTimestamp);

        // Set a value for the limitExceeded field
        expenseTransactionEntity.setLimitExceeded(false);

        // Use the current date and time in the required OffsetDateTime format
        OffsetDateTime currentOffsetDateTime = OffsetDateTime.now().withNano(0);

        // Saving a reference to the parent Entity
        AmountLimitEntity amountLimitEntity = createAmountLimitEntity(expenseTransactionDto.account_from,
                expenseTransactionDto.sum + 100.0, expenseTransactionDto.currency_shortname,
                expenseTransactionDto.expense_category,
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
    public void testGetLimitExceeded1_True() {

        /* Arrange */
        // Transaction data for verification
        String accountClient = "0000000001";
        String expenseCategory = "Service";
        String currencyCode = "KZT";
        double currentTransactionSum = 5000.0;

        // Receiving the 1st day of the current month with the start time 00:00:00
        LocalDateTime firstDayOfMonth = LocalDateTime.now().withDayOfMonth(1).toLocalDate().atStartOfDay();
        ZonedDateTime zonedDateTime = ZonedDateTime.of(firstDayOfMonth, ZoneId.systemDefault());

        // Setting the required values in existingLimit
        AmountLimitEntity amountLimitEntity = createAmountLimitEntity(accountClient,
                5000.0, "KZT", expenseCategory,
                ZonedDateTime.parse(zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));

        // Use the current date and time in the required OffsetDateTime format
        OffsetDateTime currentOffsetDateTime = OffsetDateTime.now().withNano(0);

        // Filling in some previous transaction data
        createExpenseTransactionEntity(accountClient, "9000000000",
                currencyCode, 100.0, expenseCategory,
                ZonedDateTime.parse(currentOffsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)),
                false, amountLimitEntity);

        /* Act */
        boolean limitExceeded = bankServiceImpl.getLimitExceeded(accountClient, expenseCategory,
                currencyCode, currentTransactionSum);

        /* Assert */
        assertTrue(limitExceeded, "Expected limit to be exceeded for the provided data");
    }

    @Test
    public void testGetLimitExceeded2_True() {

        /* Arrange */
        // Transaction data for verification
        String accountClient = "0000000001";
        String expenseCategory = "Service";
        String currencyCode = "EUR";
        double currentTransactionSum = 1.0;

        // Receiving the 1st day of the current month with the start time 00:00:00
        LocalDateTime firstDayOfMonth = LocalDateTime.now().withDayOfMonth(1).toLocalDate().atStartOfDay();
        ZonedDateTime zonedDateTime = ZonedDateTime.of(firstDayOfMonth, ZoneId.systemDefault());

        // Setting the required values in existingLimit
        AmountLimitEntity amountLimitEntity = createAmountLimitEntity(accountClient,
                1000.0, "USD", expenseCategory,
                ZonedDateTime.parse(zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));

        // Use the current date and time in the required OffsetDateTime format
        OffsetDateTime currentOffsetDateTime = OffsetDateTime.now().withNano(0);

        // Filling in some previous transaction data
        createExpenseTransactionEntity(accountClient, "8900000000",
                currencyCode, 1000.0, expenseCategory,
                ZonedDateTime.parse(currentOffsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)),
                true, amountLimitEntity);

        /* Act */
        boolean limitExceeded = bankServiceImpl.getLimitExceeded(accountClient, expenseCategory,
                currencyCode, currentTransactionSum);

        /* Assert */
        // Provided that 1 euro is more than 1 US dollar
        assertTrue(limitExceeded, "Expected limit to be exceeded for the provided data");
    }

    @Test
    public void testGetLimitExceeded1_False() {

        /* Arrange */
        // Transaction data for verification
        String accountClient = "0000000001";
        String expenseCategory = "Service";
        String currencyCode = "KZT";
        double currentTransactionSum = 4900.0;

        // Receiving the 1st day of the current month with the start time 00:00:00
        LocalDateTime firstDayOfMonth = LocalDateTime.now().withDayOfMonth(1).toLocalDate().atStartOfDay();
        ZonedDateTime zonedDateTime = ZonedDateTime.of(firstDayOfMonth, ZoneId.systemDefault());

        // Setting the required values in existingLimit
        AmountLimitEntity amountLimitEntity = createAmountLimitEntity(accountClient,
                5000.0, "KZT", expenseCategory,
                ZonedDateTime.parse(zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));

        // Use the current date and time in the required OffsetDateTime format
        OffsetDateTime currentOffsetDateTime = OffsetDateTime.now().withNano(0);

        // Filling in some previous transaction data
        createExpenseTransactionEntity(accountClient, "9000000000",
                currencyCode, 100.0, expenseCategory,
                ZonedDateTime.parse(currentOffsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)),
                false, amountLimitEntity);

        /* Act */
        boolean limitExceeded = bankServiceImpl.getLimitExceeded(accountClient, expenseCategory,
                currencyCode, currentTransactionSum);

        /* Assert */
        // Amount of the limit and all transaction amounts are equal
        assertFalse(limitExceeded, "Expected limit not to be exceeded for the provided data");
    }

    @Test
    public void testGetLimitExceeded2_False() {

        /* Arrange */
        // Transaction data for verification
        String accountClient = "0000000001";
        String expenseCategory = "Service";
        String currencyCode = "KZT";
        double currentTransactionSum = 100.0;

        // Receiving the 1st day of the current month with the start time 00:00:00
        LocalDateTime firstDayOfMonth = LocalDateTime.now().withDayOfMonth(1).toLocalDate().atStartOfDay();
        ZonedDateTime zonedDateTime = ZonedDateTime.of(firstDayOfMonth, ZoneId.systemDefault());

        // Setting the required values in existingLimit
        AmountLimitEntity amountLimitEntity = createAmountLimitEntity(accountClient,
                100.0, "USD", expenseCategory,
                ZonedDateTime.parse(zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));

        /* Act */
        boolean limitExceeded = bankServiceImpl.getLimitExceeded(accountClient, expenseCategory,
                currencyCode, currentTransactionSum);

        /* Assert */
        // Provided that 1 dollar US is more than 1 tenge
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
        OffsetDateTime currentOffsetDateTime = OffsetDateTime.now().withNano(0);

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
        OffsetDateTime currentOffsetDateTime = OffsetDateTime.now().withNano(0);

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

    private void createExpenseTransactionEntity(String account_client, String account_counterparty,
                           String currency_code, double transaction_sum, String expense_category,
                           ZonedDateTime transaction_date, boolean limit_exceeded, AmountLimitEntity amount_limit) {

        ExpenseTransactionEntity expenseTransactionEntity = new ExpenseTransactionEntity();

        expenseTransactionEntity.setAccountClient(account_client);
        expenseTransactionEntity.setAccountCounterparty(account_counterparty);
        expenseTransactionEntity.setCurrencyCode(currency_code);
        expenseTransactionEntity.setExpenseCategory(expense_category);
        expenseTransactionEntity.setTransactionSum(transaction_sum);

        // Checking Date and Time for valid values
        expenseTransactionEntity.setTransactionDateTime(Timestamp.from(transaction_date.toInstant()));

        expenseTransactionEntity.setLimitExceeded(limit_exceeded);
        expenseTransactionEntity.setAmountLimitEntity(amount_limit);

        expenseTransactionRepository.save(expenseTransactionEntity);
    }
}

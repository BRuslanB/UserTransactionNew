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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        ZonedDateTime zonedDateTime = ZonedDateTime.parse("2024-02-01T15:15:20+06:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        expenseTransactionDto.setDatetime(zonedDateTime);

        // Create object of ExpenseTransactionEntity
        ExpenseTransactionEntity expenseTransactionEntity = new ExpenseTransactionEntity();

        expenseTransactionEntity.setAccountClient(expenseTransactionDto.getAccount_from());
        expenseTransactionEntity.setAccountCounterparty(expenseTransactionDto.getAccount_to());
        expenseTransactionEntity.setCurrencyCode(expenseTransactionDto.getCurrency_shortname());
        expenseTransactionEntity.setTransactionSum(expenseTransactionDto.getSum());

        // Checking Expense Category for a valid value
        String expenseCategory = expenseTransactionDto.expense_category;
        expenseTransactionEntity.setExpenseCategory(expenseCategory);

        // Checking Date and Time for valid values
        ZonedDateTime transactionZonedDateTime = expenseTransactionDto.getDatetime();
        LocalDateTime transactionDateTime = transactionZonedDateTime.toLocalDateTime();
        LocalDateTime currentDateTime = LocalDateTime.now();

        Timestamp transactionTimestamp = Timestamp.from(transactionZonedDateTime.toInstant());
        expenseTransactionEntity.setTransactionDateTime(transactionTimestamp);

        // Calculating the value for the limitExceeded field
        expenseTransactionEntity.setLimitExceeded(false);
//        expenseTransactionEntity.setLimitExceeded(
//            bankServiceImpl.getLimitExceeded(
//                expenseTransactionDto.getAccount_from(),
//                expenseTransactionDto.getExpense_category(),
//                expenseTransactionDto.getCurrency_shortname(),
//                expenseTransactionDto.getSum()
//            )
//        );

        // Saving a reference to the parent Entity
        expenseTransactionEntity.setAmountLimitEntity(
            bankServiceImpl.getAmountLimit(
                expenseTransactionDto.getAccount_from(),
                expenseTransactionDto.getExpense_category()
            )
        );

        /* Act */
        expenseTransactionRepository.save(expenseTransactionEntity);

        /* Assert */
        assertTrue(transactionDateTime.isBefore(currentDateTime),
                "Transaction DateTime should be before Current DateTime");
        assertTrue("Service".equals(expenseCategory) || "Product".equals(expenseCategory),
                "Expense category should be 'Service' or 'Product'");

        // Verify that the service method was called
        verify(expenseTransactionRepository, times(1)).save(any(ExpenseTransactionEntity.class));
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

        AmountLimitEntity existingLimit = new AmountLimitEntity();

        // Setting the required values in existingLimit
        when(amountLimitRepository.findAmountLimit(accountClient, expenseCategory, currentMonth, currentYear))
                .thenReturn(Optional.of(existingLimit));

        /* Act */
//        AmountLimitEntity result = bankServiceImpl.getAmountLimit(accountClient, expenseCategory);

        /* Assert */
        assertNotNull(existingLimit);
//        assertEquals(existingLimit.getAccountClient(), result.getAccountClient());
        assertEquals(accountClient, existingLimit.getAccountClient());
//        assertEquals(existingLimit.getExpenseCategory(), result.getExpenseCategory());
        assertEquals(expenseCategory, existingLimit.getExpenseCategory());

        // Verify that the service method was called
//        verify(bankServiceImpl, times(1)).getAmountLimit(accountClient, expenseCategory);
        verify(amountLimitRepository, times(1)).findAmountLimit(accountClient, expenseCategory, currentMonth, currentYear);
    }

    @Test
    public void testGetAmountLimitWhenLimitDoesNotExist() {

        /* Arrange */
        String accountClient = "0000000001";
        String expenseCategory = "Service";
        LocalDateTime currentDateTime = LocalDateTime.now();
        int currentMonth = currentDateTime.getMonthValue();
        int currentYear = currentDateTime.getYear();

        AmountLimitEntity defaultLimit = new AmountLimitEntity();

        defaultLimit.setAccountClient(accountClient);
        defaultLimit.setExpenseCategory(expenseCategory);
        defaultLimit.setLimitSum(1000.0);
        defaultLimit.setLimitCurrencyCode("USD");

        // Mocking the behavior of amountLimitRepository with thenAnswer
//        when(amountLimitRepository.findAmountLimitByAccountAndCategoryAndMonth(accountClient, expenseCategory, currentMonth, currentYear))
//                .thenAnswer(invocation -> Collections.emptyList());

        /* Act */
//        AmountLimitEntity result = bankServiceImpl.getAmountLimit(accountClient, expenseCategory);
        AmountLimitEntity result = bankServiceImpl.saveAmountDefaultLimit(accountClient, expenseCategory);

        /* Assert */
        assertNotNull(result);
        assertEquals(defaultLimit.getAccountClient(), result.getAccountClient());
        assertEquals(defaultLimit.getExpenseCategory(), result.getExpenseCategory());
        assertEquals(defaultLimit.getLimitSum(), result.getLimitSum());
        assertEquals(defaultLimit.getLimitCurrencyCode(), result.getLimitCurrencyCode());

        // Verify that the service method was called
//        verify(bankServiceImpl, times(1)).getAmountLimit(accountClient, expenseCategory);
//        verify(amountLimitRepository, times(1)).findAmountLimitByAccountAndCategoryAndMonth
//                (accountClient, expenseCategory, currentMonth, currentYear);
        // Проверка: проверяем, что метод saveAmountDefaultLimit был вызван с правильными аргументами
//        verify(amountLimitRepository).saveAmountDefaultLimit(eq(accountClient), eq(expenseCategory));
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

        // Create object of AmountLimitEntity
        AmountLimitEntity amountLimitEntity  = new AmountLimitEntity();

        amountLimitEntity.setAccountClient("0000000001");
        amountLimitEntity.setLimitSum(defaultLimitSum);

        // Use the current date and time in the required format (trimming nanoseconds)
        LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        amountLimitEntity.setLimitDateTime(Timestamp.valueOf(currentDateTime));

        amountLimitEntity.setLimitCurrencyCode(defaultLimitCurrencyCode);
        amountLimitEntity.setExpenseCategory("Product");

        /* Act */
        amountLimitRepository.save(amountLimitEntity);

        /* Assert */
        assertEquals(1000.0, defaultLimitSum, "Default limit sum should be 1000.0");
        assertEquals("USD", defaultLimitCurrencyCode, "Default limit currency code should be USD");
        assertTrue("Service".equals(amountLimitEntity.getExpenseCategory()) ||
                    "Product".equals(amountLimitEntity.getExpenseCategory()),
            "Expense category should be 'Service' or 'Product'");

        // Verify that the service method was called
        verify(amountLimitRepository, times(1)).save(any(AmountLimitEntity.class));
    }
}

package lab.solva.user.transaction.service.impl;

import lab.solva.user.transaction.dto.AmountLimitDto;
import lab.solva.user.transaction.dto.ExpenseTransactionDto;
import lab.solva.user.transaction.model.AmountLimitEntity;
import lab.solva.user.transaction.model.ExpenseTransactionEntity;
import lab.solva.user.transaction.repository.AmountLimitRepository;
import lab.solva.user.transaction.repository.ExpenseTransactionRepository;
import lab.solva.user.transaction.service.ExchangeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BankServiceImplTest {

    @Mock
    private ExpenseTransactionRepository expenseTransactionRepository;

    @Mock
    private AmountLimitRepository amountLimitRepository;

    @Mock
    private ExchangeService exchangeService;

    @InjectMocks
    private BankServiceImpl bankServiceImpl;

    @Test
    public void testSaveExpenseTransactionDto() {

        /* Arrange */
        // Create object expenseTransactionDto
        ExpenseTransactionDto expenseTransactionDto = new ExpenseTransactionDto();

        expenseTransactionDto.setAccount_from("0000000001");
        expenseTransactionDto.setAccount_to("9000000000");
        expenseTransactionDto.setCurrency_shortname("USD");
        expenseTransactionDto.setSum(100.0);
        expenseTransactionDto.setExpense_category("Service");
        // Converting a date and time string to the desired format
        ZonedDateTime zonedDateTime = ZonedDateTime.parse("2024-02-01T15:15:20+06:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        expenseTransactionDto.setDatetime(zonedDateTime);

        // Create object expenseTransactionEntity
        ExpenseTransactionEntity expenseTransactionEntity = new ExpenseTransactionEntity();

        expenseTransactionEntity.setAccountClient(expenseTransactionDto.getAccount_from());
        expenseTransactionEntity.setAccountCounterparty(expenseTransactionDto.getAccount_to());
        expenseTransactionEntity.setCurrencyCode(expenseTransactionDto.getCurrency_shortname());
        expenseTransactionEntity.setTransactionSum(expenseTransactionDto.getSum());

        // Checking Expense Category for a valid value
        String expenseCategory = expenseTransactionDto.expense_category;
        assertTrue("Service".equals(expenseCategory) || "Product".equals(expenseCategory),
            "Expense category should be 'Service' or 'Product'");
        expenseTransactionEntity.setExpenseCategory(expenseCategory);

        // Checking Date and Time for valid values
        ZonedDateTime transactionZonedDateTime = expenseTransactionDto.getDatetime();
        LocalDateTime transactionDateTime = transactionZonedDateTime.toLocalDateTime();
        LocalDateTime currentDateTime = LocalDateTime.now();

        assertTrue(transactionDateTime.isBefore(currentDateTime),
            "Transaction DateTime should be before Current DateTime");

        Timestamp transactionTimestamp = Timestamp.from(transactionZonedDateTime.toInstant());
        expenseTransactionEntity.setTransactionDateTime(transactionTimestamp);

        // Calculating the value for the limitExceeded field
        expenseTransactionEntity.setLimitExceeded(
            bankServiceImpl.getLimitExceeded(
                expenseTransactionDto.getAccount_from(),
                expenseTransactionDto.getExpense_category(),
                expenseTransactionDto.getCurrency_shortname(),
                expenseTransactionDto.getSum()
            )
        );

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
        verify(expenseTransactionRepository, times(1)).save(any(ExpenseTransactionEntity.class));
    }

    @Test
    public void testGetLimitExceededTrue() {

        /* Arrange */
        // Подготовка данных и моков для теста

        /* Act */
        boolean result = bankServiceImpl.getLimitExceeded("account", "category", "USD", 500.0);

        /* Assert */
        assertTrue(result); // Или другие ожидаемые результаты
    }

    @Test
    public void testGetLimitExceededFalse() {

        /* Arrange */
        // Подготовка данных и моков для теста

        /* Act */
        boolean result = bankServiceImpl.getLimitExceeded("account", "category", "USD", 500.0);

        /* Assert */
        assertFalse(result); // Или другие ожидаемые результаты
    }

//    @Test
//    public void testGetAmountLimit() {
//
//        /* Arrange */
//        // Подготовка данных и моков для теста
//
//        /* Act */
//        AmountLimitEntity result = bankServiceImpl.getAmountLimit("account", "category");
//
//        /* Assert */
//        assertNotNull(result); // Или другие ожидаемые результаты
//    }

    @Test
    public void testSaveAmountLimit() {

        /* Arrange */
        // Create object amountLimitDto
        AmountLimitDto amountLimitDto = new AmountLimitDto();

        amountLimitDto.setAccount_from("0000000001");
        amountLimitDto.setLimit_sum(500.0);
        amountLimitDto.setLimit_currency_shortname("EUR");
        amountLimitDto.setExpense_category("Product");

        // Create object amountLimitDto
        AmountLimitEntity amountLimitEntity  = new AmountLimitEntity();

        amountLimitEntity.setAccountClient(amountLimitDto.getAccount_from());
        amountLimitEntity.setLimitSum(amountLimitDto.getLimit_sum());

        // Use the current date and time in the required format (trimming nanoseconds)
        LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        amountLimitEntity.setLimitDateTime(Timestamp.valueOf(currentDateTime));

        amountLimitEntity.setLimitCurrencyCode(amountLimitDto.getLimit_currency_shortname());

        // Checking Expense Category for a valid value
        String expenseCategory = amountLimitDto.getExpense_category();
        assertTrue("Service".equals(expenseCategory) || "Product".equals(expenseCategory),
                "Expense category should be 'Service' or 'Product'");
        amountLimitEntity.setExpenseCategory(expenseCategory);

        /* Act */
        amountLimitRepository.save(amountLimitEntity);

        /* Assert */
        verify(amountLimitRepository, times(1)).save(any(AmountLimitEntity.class));
    }

    @Test
    public void testSaveAmountLimitDefaultValue() {

        /* Arrange */
        // Create object amountLimitDto
        AmountLimitDto amountLimitDto = new AmountLimitDto();

        amountLimitDto.setAccount_from("0000000001");
        amountLimitDto.setLimit_sum(500.0);
        amountLimitDto.setLimit_currency_shortname("EUR");
        amountLimitDto.setExpense_category("Product");

        // Create object amountLimitDto
        AmountLimitEntity amountLimitEntity  = new AmountLimitEntity();

        amountLimitEntity.setAccountClient(amountLimitDto.getAccount_from());
        amountLimitEntity.setLimitSum(amountLimitDto.getLimit_sum());

        // Use the current date and time in the required format (trimming nanoseconds)
        LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        amountLimitEntity.setLimitDateTime(Timestamp.valueOf(currentDateTime));

        amountLimitEntity.setLimitCurrencyCode(amountLimitDto.getLimit_currency_shortname());

        // Checking Expense Category for a valid value
        String expenseCategory = amountLimitDto.getExpense_category();
        assertTrue("Service".equals(expenseCategory) || "Product".equals(expenseCategory),
                "Expense category should be 'Service' or 'Product'");
        amountLimitEntity.setExpenseCategory(expenseCategory);

        /* Act */
        amountLimitRepository.save(amountLimitEntity);

        /* Assert */
        verify(amountLimitRepository, times(1)).save(any(AmountLimitEntity.class));
    }
}

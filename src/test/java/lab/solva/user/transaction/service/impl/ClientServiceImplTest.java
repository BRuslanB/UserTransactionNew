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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Transactional
@SuppressWarnings("unused")
public class ClientServiceImplTest {

    @Autowired
    private AmountLimitRepository amountLimitRepository;

    @Autowired
    private ExpenseTransactionRepository expenseTransactionRepository;

    @Autowired
    private ClientServiceImpl clientServiceImpl;

    @Test
    public void testGetAllAmountLimitDateDtoByAccountClient() {

        /* Arrange */
        String accountClient = "0000000001";

        // Use the current date and time in the required OffsetDateTime format
        OffsetDateTime currentOffsetDateTime = OffsetDateTime.now().withNano(0);

        // Setting any required values in amountLimitEntity
        AmountLimitEntity amountLimitEntity = createAmountLimitEntity(accountClient,
                5000.0, "KZT", "Service",
                ZonedDateTime.parse(currentOffsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
        amountLimitRepository.save(amountLimitEntity);

        amountLimitEntity = createAmountLimitEntity(accountClient,
                1000.0, "EUR", "Product",
                ZonedDateTime.parse(currentOffsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
        amountLimitRepository.save(amountLimitEntity);

        /* Act */
        List<AmountLimitDateDto> actualDtoList = clientServiceImpl.getAllAmountLimitDateDtoByAccountClient(accountClient);

        /* Assert */
        assertNotNull(actualDtoList);
        assertEquals(2, actualDtoList.size());
    }

    @Test
    public void testSaveAmountLimitDto() {

        /* Arrange */
        // Create object of AmountLimitDto
        AmountLimitDto amountLimitDto = new AmountLimitDto();

        amountLimitDto.account_from = "0000000001";
        amountLimitDto.limit_sum = 500.0;
        amountLimitDto.limit_currency_shortname = "EUR";
        amountLimitDto.expense_category = "Product";

        // Create object of AmountLimitEntity
        AmountLimitEntity amountLimitEntity  = new AmountLimitEntity();

        amountLimitEntity.setAccountClient(amountLimitDto.account_from);
        amountLimitEntity.setLimitSum(amountLimitDto.limit_sum);

        // Use the current date and time in the required format (trimming nanoseconds)
        LocalDateTime currentDateTime = LocalDateTime.now().withDayOfMonth(1).toLocalDate().atStartOfDay();
        amountLimitEntity.setLimitDateTime(Timestamp.valueOf(currentDateTime));

        amountLimitEntity.setLimitCurrencyCode(amountLimitDto.limit_currency_shortname);

        // Checking Expense Category for a valid value
        String expenseCategory = amountLimitDto.expense_category;
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

        // Use the current date and time in the required OffsetDateTime format
        OffsetDateTime currentOffsetDateTime = OffsetDateTime.now().withNano(0);

        // Setting any required values in expenseTransactionEntity
        ExpenseTransactionEntity expenseTransactionEntity1 = createExpenseTransactionEntity(accountClient,
                "9000000000", "KZT", 123450.50, "Service",
                "2024-02-01T15:15:20+06:00", true,
                createAmountLimitEntity(accountClient,
                        50000.0, "KZT", "Service",
                        ZonedDateTime.parse(currentOffsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))));

        ExpenseTransactionEntity expenseTransactionEntity2 = createExpenseTransactionEntity(accountClient,
                "9800000000", "EUR", 100.90, "Service",
                "2024-02-02T16:15:20+06:00", true,
                createAmountLimitEntity(accountClient,
                        50000.0, "KZT", "Service",
                        ZonedDateTime.parse(currentOffsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))));

        // Added another expenseTransactionEntity in DB which should not be returned to the DTO
        ExpenseTransactionEntity expenseTransactionEntity3 = createExpenseTransactionEntity(accountClient,
                "8000000000", "EUR", 678.90, "Product",
                "2024-02-03T12:30:45+06:00", false,
                createAmountLimitEntity(accountClient,
                        1000.0, "EUR", "Product",
                        ZonedDateTime.parse(currentOffsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))));

        // Make expectedDtoList
        List<TransactionExceededLimitDto> expectedDtoList = List.of(
                createTransactionExceededLimitDto(accountClient, "9000000000", "KZT",
                        123450.50,
                        ZonedDateTime.parse("2024-02-01T15:15:20+06:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                        ZonedDateTime.parse(currentOffsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))),
                createTransactionExceededLimitDto(accountClient, "9800000000", "EUR",
                        100.90,
                        ZonedDateTime.parse("2024-02-02T16:15:20+06:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                        ZonedDateTime.parse(currentOffsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)))
        );

        /* Act */
        List<TransactionExceededLimitDto> actualDtoList =
                clientServiceImpl.getAllTransactionExceededLimitDtoByAccountClient(accountClient);

        /* Assert */
        assertEquals(expectedDtoList, actualDtoList);
        assertEquals(2, actualDtoList.size());
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
        ZonedDateTime transactionZonedDateTime = ZonedDateTime.parse(transaction_date,
                DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        expenseTransactionEntity.setTransactionDateTime(Timestamp.from(transactionZonedDateTime.toInstant()));

        expenseTransactionEntity.setLimitExceeded(limit_exceeded);
        expenseTransactionEntity.setAmountLimitEntity(amount_limit);

        expenseTransactionRepository.save(expenseTransactionEntity);

        return expenseTransactionEntity;
    }

    private TransactionExceededLimitDto createTransactionExceededLimitDto(String accountClient,
                                               String accountCounterparty, String currencyCode, double transactionSum,
                                               ZonedDateTime transactionDateTime, ZonedDateTime limitDateTime) {

        TransactionExceededLimitDto transactionExceededLimitDto = new TransactionExceededLimitDto();

        transactionExceededLimitDto.account_from = accountClient;
        transactionExceededLimitDto.account_to = accountCounterparty;
        transactionExceededLimitDto.currency_shortname = currencyCode;
        transactionExceededLimitDto.sum = transactionSum;
        transactionExceededLimitDto.expense_category = "Service";
        transactionExceededLimitDto.datetime = transactionDateTime.withZoneSameInstant(ZoneId.systemDefault()).toString();

        // Limit is the same for all transactions
        transactionExceededLimitDto.limit_sum = 50000.0;
        transactionExceededLimitDto.limit_currency_shortname = "KZT";
        transactionExceededLimitDto.limit_datetime = limitDateTime.withZoneSameInstant(ZoneId.systemDefault()).toString();

        return transactionExceededLimitDto;
    }
}

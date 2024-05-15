package lab.solva.user.transaction.service.impl;

import lab.solva.user.transaction.dto.ExpenseTransactionDto;
import lab.solva.user.transaction.enumeration.CurrencyType;
import lab.solva.user.transaction.enumeration.ExpenseCategory;
import lab.solva.user.transaction.model.AmountLimitEntity;
import lab.solva.user.transaction.model.ExpenseTransactionEntity;
import lab.solva.user.transaction.repository.AmountLimitRepository;
import lab.solva.user.transaction.repository.ExpenseTransactionRepository;
import lab.solva.user.transaction.service.BankService;
import lab.solva.user.transaction.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Log4j2
public class BankServiceImpl implements BankService {

    private final static double DEFAULT_LIMIT_SUM = 1000.0;
    private final static String DEFAULT_LIMIT_CURRENCY_CODE = CurrencyType.USD.name();

    private final ExpenseTransactionRepository expenseTransactionRepository;

    private final AmountLimitRepository amountLimitRepository;

    private final ExchangeService exchangeService;

    @Override
    public ExpenseTransactionEntity saveExpenseTransactionDto(ExpenseTransactionDto expenseTransactionDto) {

        // Saving received data from expenseTransactionDto
        if (expenseTransactionDto != null) {
            ExpenseTransactionEntity expenseTransactionEntity = new ExpenseTransactionEntity();
            expenseTransactionEntity.setAccountClient(expenseTransactionDto.account_from);
            expenseTransactionEntity.setAccountCounterparty(expenseTransactionDto.account_to);
            expenseTransactionEntity.setCurrencyCode(expenseTransactionDto.currency_shortname);
            expenseTransactionEntity.setTransactionSum(expenseTransactionDto.sum);

            // Checking Expense Category for a valid value
            String expenseCategory = expenseTransactionDto.expense_category;

            if (ExpenseCategory.SERVICE.name().equals(expenseCategory.toUpperCase()) ||
                    ExpenseCategory.PRODUCT.name().equals(expenseCategory.toUpperCase())) {
                expenseTransactionEntity.setExpenseCategory(expenseCategory);
            } else {
                log.error("!Invalid value, Expense Category not found in the list of valid values, " +
                          "accountClient={}, expenseCategory={}", expenseTransactionDto.account_from, expenseCategory);
                return null;
            }

            // Checking Date and Time for valid values
            ZonedDateTime transactionZonedDateTime = ZonedDateTime.parse(expenseTransactionDto.datetime,
                    DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            LocalDateTime transactionDateTime = transactionZonedDateTime.toLocalDateTime();
            LocalDateTime currentDateTime = LocalDateTime.now();

            if (transactionDateTime.isBefore(currentDateTime)) {
                Timestamp transactionTimestamp = Timestamp.from(transactionZonedDateTime.toInstant());

                expenseTransactionEntity.setTransactionDateTime(transactionTimestamp);
            } else {
                log.error("!Invalid value, Transaction Date and time is later than the Current Date and time, " +
                        "accountClient={}, transactionDateTime={}, currentDateTime={}",
                        expenseTransactionDto.account_from, transactionDateTime.toString(), currentDateTime.toString());

                return null;
            }

            // Calculating the value for the limitExceeded field
            expenseTransactionEntity.setLimitExceeded(getLimitExceeded(expenseTransactionDto.account_from,
                    expenseTransactionDto.expense_category, expenseTransactionDto.currency_shortname,
                    expenseTransactionDto.sum));

            // Saving a reference to the parent Entity
            expenseTransactionEntity.setAmountLimitEntity(getAmountLimit(expenseTransactionDto.account_from,
                    expenseTransactionDto.expense_category));

            expenseTransactionRepository.save(expenseTransactionEntity);

            log.debug("!Expense Transaction save successfully, id={}, accountClient={}",
                    expenseTransactionEntity.getId(), expenseTransactionEntity.getAccountClient());

            return expenseTransactionEntity;
        }

        return null;
    }

    protected boolean getLimitExceeded(String accountClient, String expenseCategory, String currencyCode,
                                       double currentTransactionSum) {

        // Getting the current month and year
        LocalDateTime currentDateTime = LocalDateTime.now();
        int currentMonth = currentDateTime.getMonthValue();
        int currentYear = currentDateTime.getYear();

        // Getting the limit from the database
        AmountLimitEntity amountLimitEntity = getAmountLimit(accountClient, expenseCategory);
        if (amountLimitEntity == null) {
            log.error("!Attention, Limit is not set and not received from the Database, " +
                    "accountClient={}, expenseCategory={}", accountClient, expenseCategory);
            return false;
        }

        double currentLimit = amountLimitEntity.getLimitSum();
        String limitCurrencyCode = amountLimitEntity.getLimitCurrencyCode();

        // Creating a map to hold the results for each currency
        Map<CurrencyType, Double> sumTransactionsMap = new ConcurrentHashMap<>();

        // Getting the current exchange rate from the database
        Map<CurrencyType, Double> exchangeRateMap = exchangeService.gettingRates();

        // Create ExecutorService with try-with-resources statement
        try (ExecutorService executor = Executors.newFixedThreadPool(4)) {
            List<Callable<Void>> tasks = new ArrayList<>();

            // Creating tasks to calculate the transaction sums for each currency
            for (CurrencyType currency : CurrencyType.values()) {
                tasks.add(() -> {
                    double sumTransaction = expenseTransactionRepository.calcTransactionSum(
                            accountClient, expenseCategory, currency.name(), currentMonth, currentYear);
                    sumTransactionsMap.put(currency, sumTransaction);
                    return null;
                });
            }

            // Execute all tasks in parallel for each CurrencyType.values()
            executor.invokeAll(tasks);

        } catch (InterruptedException e) {

            log.error("Error occurred while executing tasks: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }

        // Calculate the sumTransactionResult using the map
        double sumTransactionResult = sumTransactionsMap.entrySet().parallelStream()
                .mapToDouble(entry -> {
                    CurrencyType currencyType = entry.getKey();
                    double sumTransaction = entry.getValue();

                    // Add the initial transaction sum for the current currency
                    if (currencyType == CurrencyType.valueOf(currencyCode)) {
                        sumTransaction += currentTransactionSum;
                    }

                    // Converting the sumTransaction to the limitCurrencyCode
                    if (!CurrencyType.valueOf(limitCurrencyCode).equals(currencyType)) {
                        double exchangeRate = exchangeRateMap.getOrDefault(currencyType, 1.0);

                        if (!CurrencyType.valueOf(limitCurrencyCode).equals(CurrencyType.KZT)) {
                            exchangeRate /= exchangeRateMap.getOrDefault(
                                    CurrencyType.valueOf(limitCurrencyCode), 1.0);
                        }
                        sumTransaction *= exchangeRate;
                    }
                    return sumTransaction;

                })
                .sum();

        return currentLimit < sumTransactionResult;
    }

    protected AmountLimitEntity getAmountLimit(String accountClient, String expenseCategory){

        AmountLimitEntity amountLimitEntity;

        // Getting the current month and year
        LocalDateTime currentDateTime = LocalDateTime.now();
        int currentMonth = currentDateTime.getMonthValue();
        int currentYear = currentDateTime.getYear();

         // Getting the last set limit from the database
        Optional<AmountLimitEntity> lastAmountLimitOptional = amountLimitRepository.findAmountLimit(
                accountClient, expenseCategory, currentMonth, currentYear);

        // Getting a limit if there is a limit in the database, or saving the default limit
        amountLimitEntity = lastAmountLimitOptional.orElseGet(() -> saveAmountDefaultLimit(accountClient, expenseCategory));

        return amountLimitEntity;
    }

    protected AmountLimitEntity saveAmountDefaultLimit(String accountClient, String expenseCategory) {

        // Receiving the 1st day of the current month with the start time 00:00:00
        LocalDateTime firstDayOfMonth = LocalDateTime.now().withDayOfMonth(1).toLocalDate().atStartOfDay();

        AmountLimitEntity amountLimitEntity  = new AmountLimitEntity();

        amountLimitEntity.setAccountClient(accountClient);

        // Use the default value for the limit amount
        amountLimitEntity.setLimitSum(DEFAULT_LIMIT_SUM);

        // Use the value firstDayOfMonth
        amountLimitEntity.setLimitDateTime(Timestamp.valueOf(firstDayOfMonth));

        // Use the default value for the limit currency type
        amountLimitEntity.setLimitCurrencyCode(DEFAULT_LIMIT_CURRENCY_CODE);
        amountLimitEntity.setExpenseCategory(expenseCategory);

        amountLimitRepository.save(amountLimitEntity);

        log.debug("!Default Limit save successfully, id={}, accountClient={}, expenseCategory={}",
                amountLimitEntity.getId(), amountLimitEntity.getAccountClient(), amountLimitEntity.getExpenseCategory());

        return amountLimitEntity;
    }
}

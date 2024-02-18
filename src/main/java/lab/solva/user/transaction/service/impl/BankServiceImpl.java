package lab.solva.user.transaction.service.impl;

import lab.solva.user.transaction.dto.ExpenseTransactionDto;
import lab.solva.user.transaction.enumeration.CurrencyType;
import lab.solva.user.transaction.enumeration.ExpenseCategory;
import lab.solva.user.transaction.model.AmountLimitEntity;
import lab.solva.user.transaction.model.ExchangeRateEntity;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public void saveExpenseTransactionDto(ExpenseTransactionDto expenseTransactionDto) {

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
                return;
            }

            // Checking Date and Time for valid values
            ZonedDateTime transactionZonedDateTime = expenseTransactionDto.datetime;
            LocalDateTime transactionDateTime = transactionZonedDateTime.toLocalDateTime();
            LocalDateTime currentDateTime = LocalDateTime.now();

            if (transactionDateTime.isBefore(currentDateTime)) {
                Timestamp transactionTimestamp = Timestamp.from(transactionZonedDateTime.toInstant());

                expenseTransactionEntity.setTransactionDateTime(transactionTimestamp);
            } else {
                log.error("!Invalid value, Transaction Date and time is later than the Current Date and time, " +
                        "accountClient={}, transactionDateTime={}, currentDateTime={}",
                        expenseTransactionDto.account_from, transactionDateTime.toString(), currentDateTime.toString());

                return;
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
        }
    }

    protected boolean getLimitExceeded(String accountClient, String expenseCategory, String currencyCode,
                                     double currentTransactionSum) {

        // Limit amount for the current month
        double currentLimit;

        // The total amount of all transactions for the month, converted into the limit currency
        double sumTransactionResult = 0.0;

        // Getting the current month and year
        LocalDateTime currentDateTime = LocalDateTime.now();
        int currentMonth = currentDateTime.getMonthValue();
        int currentYear = currentDateTime.getYear();

        // Calculation of the amount of all transactions for a month for each currency
        double sumTransactionKZT = expenseTransactionRepository.calcTransactionSum(
                accountClient, expenseCategory, CurrencyType.KZT.name(), currentMonth, currentYear);
        double sumTransactionUSD = expenseTransactionRepository.calcTransactionSum(
                accountClient, expenseCategory, CurrencyType.USD.name(), currentMonth, currentYear);
        double sumTransactionEUR = expenseTransactionRepository.calcTransactionSum(
                accountClient, expenseCategory, CurrencyType.EUR.name(), currentMonth, currentYear);
        double sumTransactionRUB = expenseTransactionRepository.calcTransactionSum(
                accountClient, expenseCategory, CurrencyType.RUB.name(), currentMonth, currentYear);

        // Adding the current transaction amount to a specific amount
        switch (CurrencyType.valueOf(currencyCode)) {
            case KZT -> sumTransactionKZT += currentTransactionSum;
            case USD -> sumTransactionUSD += currentTransactionSum;
            case EUR -> sumTransactionEUR += currentTransactionSum;
            case RUB -> sumTransactionRUB += currentTransactionSum;

            // Other cases for other types of currencies (if necessary)

            default -> {
                // An unknown type of currency was received that is not in the database
                log.error("!Invalid value, received an unknown Currency Code, " +
                        "accountClient={}, currencyCode={}", accountClient, currencyCode);
                return false;
            }
        }

        // Getting the current exchange rate from the database
        List<ExchangeRateEntity> exchangeRateEntityList = exchangeService.gettingRates().stream().toList();

        // Create and fill a HashMap with exchange rates
        Map<String, Double> exchangeRateMap = new HashMap<>();
        for (ExchangeRateEntity exchangeRateEntity : exchangeRateEntityList) {
            exchangeRateMap.put(exchangeRateEntity.getCurrencyCode(), exchangeRateEntity.getExchangeRate());
        }

        // Getting the limit from the database
        AmountLimitEntity amountLimitEntity = getAmountLimit(accountClient, expenseCategory);
        if (amountLimitEntity != null) {
            currentLimit = amountLimitEntity.getLimitSum();
            String limitCurrencyCode = amountLimitEntity.getLimitCurrencyCode();

            // Converting all transaction amounts to the Currency Code of the set limit
            switch (CurrencyType.valueOf(limitCurrencyCode)) {
                case KZT -> {
                    sumTransactionResult = sumTransactionKZT;
                    if (exchangeRateMap.get(CurrencyType.USD.name()) != null) {
                        sumTransactionResult += sumTransactionUSD * exchangeRateMap.get(CurrencyType.USD.name());
                    }
                    if (exchangeRateMap.get(CurrencyType.EUR.name()) != null) {
                        sumTransactionResult += sumTransactionEUR * exchangeRateMap.get(CurrencyType.EUR.name());
                    }
                    if (exchangeRateMap.get(CurrencyType.RUB.name()) != null) {
                        sumTransactionResult += sumTransactionRUB * exchangeRateMap.get(CurrencyType.RUB.name());
                    }
                }
                case USD -> {
                    sumTransactionResult = sumTransactionUSD;
                    if (exchangeRateMap.get(CurrencyType.USD.name()) != null) {
                        sumTransactionResult += sumTransactionKZT / exchangeRateMap.get(CurrencyType.USD.name());
                    }
                    if (exchangeRateMap.get(CurrencyType.EUR.name()) != null &&
                            exchangeRateMap.get(CurrencyType.USD.name()) != null) {
                        sumTransactionResult += sumTransactionEUR *
                                (exchangeRateMap.get(CurrencyType.EUR.name()) / exchangeRateMap.get(CurrencyType.USD.name()));
                    }
                    if (exchangeRateMap.get(CurrencyType.RUB.name()) != null &&
                            exchangeRateMap.get(CurrencyType.USD.name()) != null) {
                        sumTransactionResult += sumTransactionRUB *
                                (exchangeRateMap.get(CurrencyType.RUB.name()) / exchangeRateMap.get(CurrencyType.USD.name()));
                    }
                }
                case EUR -> {
                    sumTransactionResult = sumTransactionEUR;
                    if (exchangeRateMap.get(CurrencyType.EUR.name()) != null) {
                        sumTransactionResult += sumTransactionKZT / exchangeRateMap.get(CurrencyType.EUR.name());
                    }
                    if (exchangeRateMap.get(CurrencyType.USD.name()) != null &&
                            exchangeRateMap.get(CurrencyType.EUR.name()) != null) {
                        sumTransactionResult += sumTransactionUSD *
                                (exchangeRateMap.get(CurrencyType.USD.name()) / exchangeRateMap.get(CurrencyType.EUR.name()));
                    }
                    if (exchangeRateMap.get(CurrencyType.RUB.name()) != null &&
                            exchangeRateMap.get(CurrencyType.EUR.name()) != null) {
                        sumTransactionResult += sumTransactionRUB *
                                (exchangeRateMap.get(CurrencyType.RUB.name()) / exchangeRateMap.get(CurrencyType.EUR.name()));
                    }
                }
                case RUB -> {
                    sumTransactionResult = sumTransactionRUB;
                    if (exchangeRateMap.get(CurrencyType.RUB.name()) != null) {
                        sumTransactionResult += sumTransactionKZT / exchangeRateMap.get(CurrencyType.RUB.name());
                    }
                    if (exchangeRateMap.get(CurrencyType.USD.name()) != null &&
                            exchangeRateMap.get(CurrencyType.RUB.name()) != null) {
                        sumTransactionResult += sumTransactionUSD *
                                (exchangeRateMap.get(CurrencyType.USD.name()) / exchangeRateMap.get(CurrencyType.RUB.name()));
                    }
                    if (exchangeRateMap.get(CurrencyType.EUR.name()) != null &&
                            exchangeRateMap.get(CurrencyType.RUB.name()) != null) {
                        sumTransactionResult += sumTransactionEUR *
                                (exchangeRateMap.get(CurrencyType.EUR.name()) / exchangeRateMap.get(CurrencyType.RUB.name()));
                    }
                }

                // Other cases for other types of currencies (if necessary)
            }
        } else {
            log.error("!Attention, Limit is not set and not received from the Database, " +
                    "accountClient={}, expenseCategory={}", accountClient, expenseCategory);

            return false;
        }

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

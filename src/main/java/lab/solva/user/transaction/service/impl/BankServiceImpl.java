package lab.solva.user.transaction.service.impl;

import lab.solva.user.transaction.dto.ExpenseTransactionDto;
import lab.solva.user.transaction.enumeration.CurrencyType;
import lab.solva.user.transaction.model.AmountLimitEntity;
import lab.solva.user.transaction.model.ExchangeRateEntity;
import lab.solva.user.transaction.model.ExpenseTransactionEntity;
import lab.solva.user.transaction.repository.AmountLimitRepository;
import lab.solva.user.transaction.repository.ExpenseTransactionRepository;
import lab.solva.user.transaction.service.BankService;
import lab.solva.user.transaction.service.XmlParserExchange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BankServiceImpl implements BankService {

    private final static double DEFAULT_LIMIT = 1000.0;
    private final static String DEFAULT_CURRENCY_CODE = CurrencyType.USD.name();

    private final ExpenseTransactionRepository expenseTransactionRepository;
    private final AmountLimitRepository amountLimitRepository;
    private final XmlParserExchange xmlParserExchange;

    @Override
    public void saveExpenseTransactionDto(ExpenseTransactionDto expenseTransactionDto) {
        // Сохранение полученных данных из expenseTransactionDto
        if (expenseTransactionDto != null) {
            ExpenseTransactionEntity expenseTransactionEntity = new ExpenseTransactionEntity();
            expenseTransactionEntity.setAccountClient(expenseTransactionDto.account_to);
            expenseTransactionEntity.setAccountCounterparty(expenseTransactionDto.account_from);
            expenseTransactionEntity.setCurrencyCode(expenseTransactionDto.currency_shortname);
            expenseTransactionEntity.setTransactionSum(expenseTransactionDto.Sum);
            expenseTransactionEntity.setExpenseCategory(expenseTransactionDto.expense_category);
            expenseTransactionEntity.setTransactionDateTime(expenseTransactionDto.datetime);
            // Вычисление значение для поля limitExceeded
            expenseTransactionEntity.setLimitExceeded(getLimitExceeded(expenseTransactionDto.account_to,
                    expenseTransactionDto.expense_category, expenseTransactionDto.currency_shortname,
                    expenseTransactionDto.Sum));
            // Сохраняем ссылку на родительскую сущность
            expenseTransactionEntity.setAmountLimitEntity(getAmountLimit(expenseTransactionDto.account_to,
                    expenseTransactionDto.expense_category));
            // Логирование действия
            expenseTransactionRepository.save(expenseTransactionEntity);
        }
    }

    private boolean getLimitExceeded(String accountClient, String expenseCategory, String currencyCode,
                                     double currentTransactionSum){

        double currentLimit; // Сумма лимита за текущий месяц
        double sumTransactionResult = 0.0; // Сумма всех транзакции за месяц

        // Получение текущего месяца и года
        LocalDateTime currentDateTime = LocalDateTime.now();
        int currentMonth = currentDateTime.getMonthValue();
        int currentYear = currentDateTime.getYear();

        // Вычисление суммы всех транзакции за месяц по всем валютам
        double sumTransactionKZT = expenseTransactionRepository.calcTransactionSum(
                accountClient, expenseCategory, CurrencyType.KZT.name(), currentMonth, currentYear);
        double sumTransactionUSD = expenseTransactionRepository.calcTransactionSum(
                accountClient, expenseCategory, CurrencyType.USD.name(), currentMonth, currentYear);
        double sumTransactionEUR = expenseTransactionRepository.calcTransactionSum(
                accountClient, expenseCategory, CurrencyType.EUR.name(), currentMonth, currentYear);
        double sumTransactionRUB = expenseTransactionRepository.calcTransactionSum(
                accountClient, expenseCategory, CurrencyType.RUB.name(), currentMonth, currentYear);

        // Добавляем сумму по текущей транзакции
        switch (CurrencyType.valueOf(currencyCode)) {
            case KZT:
                sumTransactionKZT += currentTransactionSum;
                break;
            case USD:
                sumTransactionUSD += currentTransactionSum;
                break;
            case EUR:
                sumTransactionEUR += currentTransactionSum;
                break;
            case RUB:
                sumTransactionRUB += currentTransactionSum;
                break;
            // Другие кейсы для остальных видов валют (при необходимости)
            default:
                // Получен неизвестный вид валюты
                // Сохраняем этот факт в Лог
                return false;
        }

        // Получение текущего курса валют из БД
        List<ExchangeRateEntity> exchangeRateEntityList = xmlParserExchange.gettingRates().stream().toList();
        // Создаем и заполняем HashMap с курсами валют
        Map<String, Double> exchangeRateMap = new HashMap<>();
        for (ExchangeRateEntity exchangeRateEntity : exchangeRateEntityList) {
            exchangeRateMap.put(exchangeRateEntity.getCurrencyCode(), exchangeRateEntity.getExchangeRate());
        }

        // Приведение всех сумм к DEFAULT_CURRENCY_CODE лимита
        switch (CurrencyType.valueOf(DEFAULT_CURRENCY_CODE)) {
            case KZT:
                sumTransactionResult = sumTransactionKZT ;
                if (exchangeRateMap.get(CurrencyType.USD.name()) != null) {
                    sumTransactionResult += sumTransactionUSD * exchangeRateMap.get(CurrencyType.USD.name());
                }
                if (exchangeRateMap.get(CurrencyType.EUR.name()) != null) {
                    sumTransactionResult += sumTransactionEUR * exchangeRateMap.get(CurrencyType.EUR.name());
                }
                if (exchangeRateMap.get(CurrencyType.RUB.name()) != null) {
                    sumTransactionResult += sumTransactionRUB * exchangeRateMap.get(CurrencyType.RUB.name());
                }
                break;
            case USD:
                sumTransactionResult = sumTransactionUSD ;
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
                break;
            case EUR:
                sumTransactionResult = sumTransactionEUR ;
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
                break;
            case RUB:
                sumTransactionResult = sumTransactionRUB ;
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
                    sumTransactionResult += sumTransactionRUB *
                            (exchangeRateMap.get(CurrencyType.EUR.name()) / exchangeRateMap.get(CurrencyType.RUB.name()));
                }
                break;
            // Другие кейсы для остальных видов валют (при необходимости)
        }

        AmountLimitEntity amountLimitEntity = getAmountLimit(accountClient, expenseCategory);
        if (amountLimitEntity != null)
            currentLimit = amountLimitEntity.getLimitSum();
        else {
            // Лимит не получен
            // Сохраняем этот факт в Лог
            return false;
        }

        return currentLimit < sumTransactionResult;

    }
    private AmountLimitEntity getAmountLimit(String accountClient, String expenseCategory){

        AmountLimitEntity amountLimitEntity;

        // Получение текущего месяца и года
        LocalDateTime currentDateTime = LocalDateTime.now();
        int currentMonth = currentDateTime.getMonthValue();
        int currentYear = currentDateTime.getYear();

         // Получение последнего установленного лимита из БД
        Optional<AmountLimitEntity> lastAmountLimitOptional = amountLimitRepository.findAmountLimit(
                accountClient, expenseCategory, currentMonth, currentYear);
        // Получаем лимит если лимит есть в БД, или сохраняем лимит заданный по умолчанию
        amountLimitEntity = lastAmountLimitOptional.orElseGet(() -> saveAmountLimit(accountClient, expenseCategory));

        return amountLimitEntity;
    }

    private AmountLimitEntity saveAmountLimit(String accountClient, String expenseCategory) {
        // Получаем 1 число текущего месяца с временем 00:00:00
        LocalDateTime firstDayOfMonth = LocalDateTime.now().withDayOfMonth(1).toLocalDate().atStartOfDay();

        AmountLimitEntity amountLimitEntity  = new AmountLimitEntity();
        amountLimitEntity.setAccountClient(accountClient);
        // Используем значение по умолчанию для суммы лимита
        amountLimitEntity.setLimitSum(DEFAULT_LIMIT);
        // Используем значение firstDayOfMonth
        amountLimitEntity.setLimitDateTime(Timestamp.valueOf(firstDayOfMonth));
        // Используем значение по умолчанию для вида валюты лимита
        amountLimitEntity.setLimitCurrencyCode(DEFAULT_CURRENCY_CODE);
        amountLimitEntity.setExpenseCategory(expenseCategory);
        // Логирование действия
        amountLimitRepository.save(amountLimitEntity);

        return amountLimitEntity;
    }
}

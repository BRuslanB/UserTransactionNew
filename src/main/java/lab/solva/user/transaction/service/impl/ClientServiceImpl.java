package lab.solva.user.transaction.service.impl;

import lab.solva.user.transaction.dto.AmountLimitDateDto;
import lab.solva.user.transaction.dto.AmountLimitDto;
import lab.solva.user.transaction.dto.TransactionExceededLimitDto;
import lab.solva.user.transaction.enumeration.ExpenseCategory;
import lab.solva.user.transaction.model.AmountLimitEntity;
import lab.solva.user.transaction.repository.AmountLimitRepository;
import lab.solva.user.transaction.repository.ExpenseTransactionRepository;
import lab.solva.user.transaction.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final AmountLimitRepository amountLimitRepository;
    private final ExpenseTransactionRepository expenseTransactionRepository;
    @Override
    public List<AmountLimitDateDto> getAllAmountLimitDateDto() {
        // Получение всех лимитов из БД
        List<AmountLimitEntity> amountLimitEntityList = amountLimitRepository.findAllByOrderByLimitDateTimeDesc();
        List<AmountLimitDateDto> amountLimitDateDtoList = new ArrayList<>();
        if (amountLimitEntityList.size() > 0) {
            for (AmountLimitEntity amountLimitEntity : amountLimitEntityList) {
                AmountLimitDateDto amountLimitDateDto = new AmountLimitDateDto();
                amountLimitDateDto.account_from = amountLimitEntity.getAccountClient();
                amountLimitDateDto.limit_sum = amountLimitEntity.getLimitSum();
                amountLimitDateDto.limit_currency_shortname = amountLimitEntity.getLimitCurrencyCode();
                amountLimitDateDto.expense_category = amountLimitEntity.getExpenseCategory();
                // Преобразование Timestamp в ZonedDateTime
                amountLimitDateDto.limit_datetime = amountLimitEntity.getLimitDateTime().
                        toInstant().atZone(ZoneId.systemDefault());
                amountLimitDateDtoList.add(amountLimitDateDto);
            }
        }
        return amountLimitDateDtoList;
    }

    @Override
    public void setAmountLimitDto(AmountLimitDto amountLimitDto) {
        // Сохранение полученных данных из amountLimitDto
        if (amountLimitDto != null) {
            AmountLimitEntity amountLimitEntity  = new AmountLimitEntity();
            amountLimitEntity.setAccountClient(amountLimitDto.account_from);
            amountLimitEntity.setLimitSum(amountLimitDto.limit_sum);
            // Используем текущую дату и время в нужном формате (обрезка наносекунд)
            LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
            amountLimitEntity.setLimitDateTime(Timestamp.valueOf(currentDateTime));
            amountLimitEntity.setLimitCurrencyCode(amountLimitDto.limit_currency_shortname);
            // Проверка Expense Category на допустимое заначение
            String expenseCategory = amountLimitDto.expense_category;
            if (ExpenseCategory.SERVICE.name().equals(expenseCategory.toUpperCase()) ||
                    ExpenseCategory.PRODUCT.name().equals(expenseCategory.toUpperCase())) {
                amountLimitEntity.setExpenseCategory(expenseCategory);
            } else {
                // Не допустимое значение
                // Сохраняем этот факт в Лог
                return;
            }
            // Логирование действия
            amountLimitRepository.save(amountLimitEntity);
        }
    }

    @Override
    public List<TransactionExceededLimitDto> getTransactionExceededLimitDto() {
        // Получение списка транзакций, превысивших лимит из БД
        List<Object[]> result = expenseTransactionRepository.findAllTransactionWithExceededLimit();
        List<TransactionExceededLimitDto> transactionExceededLimitDtoList = new ArrayList<>();

        for (Object[] objects : result) {
            String accountClient = (String) objects[0];
            String accountCounterparty = (String) objects[1];
            String currencyCode = (String) objects[2];
            Double transactionSum = (Double) objects[3];
            String expenseCategory = (String) objects[4];
            // Преобразование Timestamp в ZonedDateTime
            Timestamp transactionDateTimeTimestamp = (Timestamp) objects[5];
            ZonedDateTime transactionDateTime = transactionDateTimeTimestamp.toInstant().atZone(ZoneId.systemDefault());
            Double limitSum = (Double) objects[6];
            // Преобразование Timestamp в ZonedDateTime
            Timestamp limitDateTimeTimestamp = (Timestamp) objects[7];
            ZonedDateTime limitDateTime = limitDateTimeTimestamp.toInstant().atZone(ZoneId.systemDefault());
            String limitCurrencyCode = (String) objects[8];

            TransactionExceededLimitDto transactionExceededLimitDto = new TransactionExceededLimitDto(
                    accountClient, accountCounterparty, currencyCode, transactionSum,
                    expenseCategory, transactionDateTime, limitSum, limitDateTime, limitCurrencyCode
            );
            transactionExceededLimitDtoList.add(transactionExceededLimitDto);
        }

        return transactionExceededLimitDtoList;
    }
}

package lab.solva.user.transaction.service.impl;

import lab.solva.user.transaction.dto.AmountLimitDateDto;
import lab.solva.user.transaction.dto.AmountLimitDto;
import lab.solva.user.transaction.dto.ExpenseTransactionDto;
import lab.solva.user.transaction.dto.TransactionExceededLimitDto;
import lab.solva.user.transaction.model.AmountLimitEntity;
import lab.solva.user.transaction.repository.AmountLimitRepository;
import lab.solva.user.transaction.repository.ExpenseTransactionRepository;
import lab.solva.user.transaction.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
                amountLimitDateDto.limit_sum = amountLimitEntity.getLimit_sum();
                amountLimitDateDto.limit_currency_shortname = amountLimitEntity.getLimitCurrencyCode();
                amountLimitDateDto.limit_datetime = amountLimitEntity.getLimitDateTime();
            };
        }
        return amountLimitDateDtoList;
    }

    @Override
    public void setAmountLimitDto(AmountLimitDto amountLimitDto) {
        // Сохранение полученных данных из amountLimitDto
        if (amountLimitDto != null) {
            AmountLimitEntity amountLimitEntity  = new AmountLimitEntity();
            amountLimitEntity.setLimit_sum(amountLimitDto.limit_sum);
            // Используем текущую дату и время в нужном формате
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX");
            amountLimitEntity.setLimitDateTime(Timestamp.valueOf(currentDateTime.format(formatter)));
            amountLimitEntity.setLimitCurrencyCode(amountLimitDto.limit_currency_shortname);
            amountLimitEntity.setExpenseCategory(amountLimitDto.expense_category);
            // Логирование действия
            amountLimitRepository.save(amountLimitEntity);
        }
    }

    @Override
    public List<TransactionExceededLimitDto> getTransactionExceededLimitDto() {
        // Получение списка транзакций, превысивших лимит из БД
//        List<TransactionExceededLimitDto> transactionExceededLimitDtoList =
//                expenseTransactionRepository.findTransactionsWithLimits();
//        if (transactionExceededLimitDtoList.size() > 0) {
//            return transactionExceededLimitDtoList;
//        }
        return null;
    }
}

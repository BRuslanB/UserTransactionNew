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
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class ClientServiceImpl implements ClientService {

    private final AmountLimitRepository amountLimitRepository;
    private final ExpenseTransactionRepository expenseTransactionRepository;

    @Override
    public List<AmountLimitDateDto> getAllAmountLimitDateDtoByAccountClient(String accountClient) {

        // Getting all limits from the database
        List<AmountLimitEntity> amountLimitEntityList = amountLimitRepository.findAllAmountLimitByAccount(accountClient);
        List<AmountLimitDateDto> amountLimitDateDtoList = new ArrayList<>();

        if (amountLimitEntityList.size() > 0) {

            for (AmountLimitEntity amountLimitEntity : amountLimitEntityList) {
                AmountLimitDateDto amountLimitDateDto = new AmountLimitDateDto();

                amountLimitDateDto.account_from = amountLimitEntity.getAccountClient();
                amountLimitDateDto.limit_sum = amountLimitEntity.getLimitSum();
                amountLimitDateDto.limit_currency_shortname = amountLimitEntity.getLimitCurrencyCode();
                amountLimitDateDto.expense_category = amountLimitEntity.getExpenseCategory();

                // Convert Timestamp to ZonedDateTime
                amountLimitDateDto.limit_datetime = amountLimitEntity.getLimitDateTime().
                        toInstant().atZone(ZoneId.systemDefault());
                amountLimitDateDtoList.add(amountLimitDateDto);
            }
        }

        log.debug("!Getting all Limits from the Database, accountClient={}", accountClient);

        return amountLimitDateDtoList;
    }

    @Override
    public AmountLimitEntity saveAmountLimitDto(AmountLimitDto amountLimitDto) {

        // Saving received data from amountLimitDto
        if (amountLimitDto != null) {
            AmountLimitEntity amountLimitEntity  = new AmountLimitEntity();

            amountLimitEntity.setAccountClient(amountLimitDto.account_from);
            amountLimitEntity.setLimitSum(amountLimitDto.limit_sum);

            // Use the current date and time in the required format (trimming nanoseconds)
            LocalDateTime currentDateTime = LocalDateTime.now().withSecond(0).withNano(0);
            amountLimitEntity.setLimitDateTime(Timestamp.valueOf(currentDateTime));
            amountLimitEntity.setLimitCurrencyCode(amountLimitDto.limit_currency_shortname);

            // Checking Expense Category for a valid value
            String expenseCategory = amountLimitDto.expense_category;

            if (ExpenseCategory.SERVICE.name().equals(expenseCategory.toUpperCase()) ||
                    ExpenseCategory.PRODUCT.name().equals(expenseCategory.toUpperCase())) {
                amountLimitEntity.setExpenseCategory(expenseCategory);
            } else {
                log.error("!Invalid value, Expense Category not found in the list of valid values, " +
                        "accountClient={}, expenseCategory={}", amountLimitDto.account_from, expenseCategory);

                return null;
            }

            amountLimitRepository.save(amountLimitEntity);

            log.debug("!Amount Limit save successfully, id={}, accountClient={}, expenseCategory={}",
                    amountLimitEntity.getId(), amountLimitEntity.getAccountClient(), amountLimitEntity.getExpenseCategory());

            return amountLimitEntity;
        }

        return null;
    }

    @Override
    public List<TransactionExceededLimitDto> getAllTransactionExceededLimitDtoByAccountClient(String accountFrom) {

        // Getting a list of transactions from the database that exceeded the limit
        List<Object[]> result = expenseTransactionRepository.findAllTransactionWithExceededLimit(accountFrom);
        List<TransactionExceededLimitDto> transactionExceededLimitDtoList = new ArrayList<>();

        for (Object[] objects : result) {
            String accountClient = (String) objects[0];
            String accountCounterparty = (String) objects[1];
            String currencyCode = (String) objects[2];
            Double transactionSum = (Double) objects[3];
            String expenseCategory = (String) objects[4];

            // Convert Timestamp to ZonedDateTime
            Timestamp transactionDateTimeTimestamp = (Timestamp) objects[5];
            ZonedDateTime transactionDateTime = transactionDateTimeTimestamp.toInstant().atZone(ZoneId.systemDefault());
            Double limitSum = (Double) objects[6];

            // Convert Timestamp to ZonedDateTime
            Timestamp limitDateTimeTimestamp = (Timestamp) objects[7];
            ZonedDateTime limitDateTime = limitDateTimeTimestamp.toInstant().atZone(ZoneId.systemDefault());
            String limitCurrencyCode = (String) objects[8];

            TransactionExceededLimitDto transactionExceededLimitDto = new TransactionExceededLimitDto(
                    accountClient, accountCounterparty, currencyCode, transactionSum,
                    expenseCategory, transactionDateTime, limitSum, limitDateTime, limitCurrencyCode
            );
            transactionExceededLimitDtoList.add(transactionExceededLimitDto);
        }

        log.debug("!Getting a list of Transactions from the Database that Exceeded the Limit, " +
                        "accountClient={}", accountFrom);

        return transactionExceededLimitDtoList;
    }
}

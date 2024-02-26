package lab.solva.user.transaction.resolver;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lab.solva.user.transaction.dto.AmountLimitDateDto;
import lab.solva.user.transaction.dto.TransactionExceededLimitDto;
import lab.solva.user.transaction.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
@SuppressWarnings("unused")
public class QueryResolver implements GraphQLQueryResolver {

    private final ClientService clientService;

    public List<AmountLimitDateDto> getAllAmountLimitDateByAccountClient(String accountClient) {
        log.debug("!Call method getting all Limits from the Database");

        // Return list of AmountLimitDateDto
        return clientService.getAllAmountLimitDateDtoByAccountClient(accountClient);
    }

    public List<TransactionExceededLimitDto> getAllTransactionExceededLimitByAccountClient(String accountClient) {
        log.debug("!Call method getting a list of all transactions that exceeded the established limit from the database");

        // Return list of TransactionExceededLimitDto
        return clientService.getAllTransactionExceededLimitDtoByAccountClient(accountClient);
    }
}

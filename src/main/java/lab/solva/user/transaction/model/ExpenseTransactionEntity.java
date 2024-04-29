package lab.solva.user.transaction.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "t_expense_transaction")
public class ExpenseTransactionEntity extends BaseEntity {

    @Size(max = 10)
    @Column(name = "account_client", nullable = false)
    private String accountClient;

    @Size(max = 10)
    @Column(name = "account_counterparty", nullable = false)
    private String accountCounterparty;

    @Size(max = 3)
    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    @Column(name = "transaction_sum", nullable = false)
    private double transactionSum;

    @Column(name = "expense_category", nullable = false)
    private String expenseCategory;

    @Column(name = "transaction_date", nullable = false,
            columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Timestamp transactionDateTime;

    @Column(name = "limit_exceeded", nullable = false)
    private boolean limitExceeded;

    @ManyToOne
    @JoinColumn(name = "amount_limit_id")
    private AmountLimitEntity amountLimitEntity;
}

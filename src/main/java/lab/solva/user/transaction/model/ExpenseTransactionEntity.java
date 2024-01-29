package lab.solva.user.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Entity
@Table(name = "t_expense_transaction")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 10)
    @Column(name = "account_client")
    private String accountClient;

    @Size(max = 10)
    @Column(name = "account_counterparty")
    private String accountCounterparty;

    @Size(max = 3)
    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "transaction_sum")
    private double transactionSum;

    @Column(name = "expense_category")
    private String expenseCategory;

    @Column(name = "transaction_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Timestamp transactionDateTime;

    @Column(name = "limit_exceeded")
    private boolean limitExceeded;

    @ManyToOne
    @JoinColumn(name = "amount_limit_id")
    private AmountLimitEntity amountLimitEntity;
}

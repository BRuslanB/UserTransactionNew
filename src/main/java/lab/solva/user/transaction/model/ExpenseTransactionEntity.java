package lab.solva.user.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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

    @Column(name = "account_client")
    private String accountClient;

    @Column(name = "account_counterparty")
    private String accountCounterparty;

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
}

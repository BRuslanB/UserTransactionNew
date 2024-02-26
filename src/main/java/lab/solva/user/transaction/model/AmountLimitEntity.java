package lab.solva.user.transaction.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "t_amount_limit")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AmountLimitEntity extends BaseEntity {

    @Size(max = 10)
    @Column(name = "account_client", nullable = false)
    private String accountClient;

    @Column(name = "limit_sum", nullable = false)
    private double limitSum;

    @Column(name = "limit_date", nullable = false,
            columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Timestamp limitDateTime;

    @Size(max = 3)
    @Column(name = "limit_currency_code", nullable = false)
    private String limitCurrencyCode;

    @Size(max = 10)
    @Column(name = "expense_category", nullable = false)
    private String expenseCategory;

    @OneToMany(mappedBy = "amountLimitEntity", fetch = FetchType.EAGER,
            cascade = CascadeType.REMOVE)
    private Set<ExpenseTransactionEntity> expenseTransactionEntities;
}

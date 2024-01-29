package lab.solva.user.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "t_amount_limit")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AmountLimitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 10)
    @Column(name = "account_client")
    private String accountClient;

    @Column(name = "limit_sum")
    private double limitSum;

    @Column(name = "limit_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Timestamp limitDateTime;

    @Size(max = 3)
    @Column(name = "limit_currency_code")
    private String limitCurrencyCode;

    @Column(name = "expense_category")
    private String expenseCategory;

    @OneToMany(mappedBy = "amountLimitEntity", fetch = FetchType.EAGER,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ExpenseTransactionEntity> expenseTransactionEntities;
}

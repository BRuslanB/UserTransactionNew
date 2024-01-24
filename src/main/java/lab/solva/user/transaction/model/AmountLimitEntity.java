package lab.solva.user.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

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

    @Column(name = "limit_sum")
    private double limit_sum;

    @Column(name = "limit_date")
    private Timestamp limitDateTime;

    @Column(name = "limit_currency_code")
    private String limitCurrencyCode;

    @Column(name = "expense_category")
    private String expenseCategory;
}

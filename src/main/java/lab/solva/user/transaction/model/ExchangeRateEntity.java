package lab.solva.user.transaction.model;

import jakarta.persistence.*;
import lombok.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "t_exchange_rate")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateEntity extends BaseEntity {

    @Column(name = "currency_name", nullable = false)
    private String currencyName;

    @Size(max = 3)
    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    @Column(name = "exchange_rate", nullable = false)
    private double exchangeRate;

    @ManyToOne
    @JoinColumn(name = "exchange_info_id")
    private ExchangeInfoEntity exchangeInfoEntity;
}

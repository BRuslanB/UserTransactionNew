package lab.solva.user.transaction.model;

import javax.persistence.*;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "t_exchange_rate")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "currency_name")
    private String currencyName;

    @Size(max = 3)
    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "exchange_rate")
    private double exchangeRate;

    @ManyToOne
    @JoinColumn(name = "exchange_info_id")
    private ExchangeInfoEntity exchangeInfoEntity;
}

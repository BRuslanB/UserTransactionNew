package lab.solva.user.transaction.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "t_exchange_info")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "resource")
    private String resource;

    @Column(name = "request_date")
    private LocalDate requestDate;

    @OneToMany(mappedBy = "exchangeInfo", fetch = FetchType.EAGER,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ExchangeRateEntity> exchangeRates;
}

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
public class ExchangeInfoEntity extends BaseEntity {

    @Column(name = "resource", nullable = false)
    private String resource;

    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;

    @OneToMany(mappedBy = "exchangeInfoEntity", fetch = FetchType.EAGER,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ExchangeRateEntity> exchangeRateEntities;
}

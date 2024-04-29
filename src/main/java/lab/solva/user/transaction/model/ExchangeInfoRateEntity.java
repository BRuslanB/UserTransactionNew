package lab.solva.user.transaction.model;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(value = "exchange_info_rate")
public class ExchangeInfoRateEntity {

    @PrimaryKeyColumn(name = "request_date", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private LocalDate requestDate;

    @PrimaryKeyColumn(name = "is_closed", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private boolean isClosed;

    @Column(value = "resource")
    private String resource;

    @Column(value = "exchange_rates")
    private Map<String, Double> exchangeRates; // currencyCode -> exchangeRate
}

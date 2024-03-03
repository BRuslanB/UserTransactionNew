package lab.solva.user.transaction.service;

import io.grpc.stub.StreamObserver;
import lab.solva.user.transaction.ExchangeRatesRequest;
import lab.solva.user.transaction.ExchangeRatesResponse;
import lab.solva.user.transaction.dto.ExchangeRateDto;
import lab.solva.user.transaction.model.ExchangeRateEntity;

import java.util.List;
import java.util.Set;

public interface ExchangeService {

    // Receiving all exchange rates for the latest date from an external service and saving in the database
    void gettingRates(ExchangeRatesRequest request, StreamObserver<ExchangeRatesResponse> responseObserver);

    Set<ExchangeRateEntity> gettingRates();
}

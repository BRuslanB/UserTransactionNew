package lab.solva.user.transaction.service;

import io.grpc.stub.StreamObserver;
import lab.solva.user.transaction.ExchangeInfoRequest;
import lab.solva.user.transaction.ExchangeInfoResponse;
import lab.solva.user.transaction.model.ExchangeRateEntity;

import java.util.Set;

public interface ExchangeService {

    // Receiving all exchange rates for the latest date from an external service and saving in the database
    void gettingRates(ExchangeInfoRequest request, StreamObserver<ExchangeInfoResponse> responseObserver);

    Set<ExchangeRateEntity> gettingRates();
}

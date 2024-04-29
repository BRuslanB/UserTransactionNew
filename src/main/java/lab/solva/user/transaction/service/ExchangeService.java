package lab.solva.user.transaction.service;

import io.grpc.stub.StreamObserver;
import lab.solva.user.transaction.ExchangeInfoRequest;
import lab.solva.user.transaction.ExchangeInfoResponse;

import java.util.Map;

public interface ExchangeService {

    // Receiving all exchange rates for the latest date from an external service and saving in the database
    void gettingRates(ExchangeInfoRequest request, StreamObserver<ExchangeInfoResponse> responseObserver);

    // Receiving all exchange rates for the latest date from the database
    Map<String, Double> gettingRates();
}

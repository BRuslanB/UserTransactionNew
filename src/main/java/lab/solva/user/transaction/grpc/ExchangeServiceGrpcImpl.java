package lab.solva.user.transaction.grpc;

import io.grpc.stub.StreamObserver;
import lab.solva.user.transaction.ExchangeRatesRequest;
import lab.solva.user.transaction.ExchangeRatesResponse;
import lab.solva.user.transaction.ExchangeServiceGrpc;
import lab.solva.user.transaction.service.ExchangeService;
import net.devh.boot.grpc.server.service.GrpcService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@GrpcService
@RequiredArgsConstructor
@Log4j2
public class ExchangeServiceGrpcImpl extends ExchangeServiceGrpc.ExchangeServiceImplBase {

    private final ExchangeService exchangeService;

    @Override
    public void gettingRates(ExchangeRatesRequest request, StreamObserver<ExchangeRatesResponse> responseObserver) {

        log.debug("!Call gRPC method getting all Exchange Rates for the Latest Date from the Database");
        exchangeService.gettingRates(request, responseObserver);
    }
}

package lab.solva.user.transaction.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lab.solva.user.transaction.dto.ExchangeRateDto;
import lab.solva.user.transaction.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/exchange")
@CrossOrigin
@Log4j2
@RequiredArgsConstructor
@Tag(name = "Exchange", description = "All methods of Exchange Services")
public class ExchangeController {

    private final ExchangeService exchangeService;

    @GetMapping
    @Operation(description = "Retrieving all exchange rates for the current date from the database")
    public List<ExchangeRateDto> getAllExchangeRateByCurrentDate(){

        log.debug("!Call method getting all exchange rates for the current date from the database");
        return exchangeService.getAllExchangeRateDtoByCurrentDate();
    }
}

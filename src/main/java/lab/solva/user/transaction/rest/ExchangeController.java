package lab.solva.user.transaction.rest;

import lab.solva.user.transaction.dto.ExchangeRateDto;
import lab.solva.user.transaction.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/exchange")
@CrossOrigin
@RequiredArgsConstructor
public class ExchangeController {

    private final ExchangeService exchangeService;

    @GetMapping
    // Получение всех курсов валют на текущую дату из БД
    public List<ExchangeRateDto> getAllExchangeRateByCurrentDate(){
        // Используется для просмотра результата
        // Логирование действия
        return exchangeService.getAllExchangeRateDtoByCurrentDate();
    }
}

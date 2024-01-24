package lab.solva.user.transaction.rest;

import lab.solva.user.transaction.dto.ExchangeRateDto;
import lab.solva.user.transaction.model.ExchangeRateEntity;
import lab.solva.user.transaction.service.XmlParserExchange;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api/exchange")
@CrossOrigin
@RequiredArgsConstructor
public class ExchangeController {

    private final XmlParserExchange xmlParserExchange;

    @GetMapping
    public List<ExchangeRateDto> getAllExchangeRate(){
        // Для просмотра результата
        // Логирование действия
        return xmlParserExchange.getAllExchangeRateDto();
    }
}

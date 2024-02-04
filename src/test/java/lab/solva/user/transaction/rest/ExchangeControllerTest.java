package lab.solva.user.transaction.rest;

import lab.solva.user.transaction.dto.ExchangeRateDto;
import lab.solva.user.transaction.service.ExchangeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ExchangeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeService exchangeService;

    @Test
    public void testGetAllExchangeRateByCurrentDate() throws Exception {

        // Arrange
        List<ExchangeRateDto> exchangeRateDtoList = Arrays.asList(
                createExchangeRateDto("USD", "ДОЛЛАР США", 449.98),
                createExchangeRateDto("EUR", "ЕВРО", 487.0),
                createExchangeRateDto("RUB", "РОССИЙСКИЙ РУБЛЬ", 5.02)
        );

        when(exchangeService.getAllExchangeRateDtoByCurrentDate()).thenReturn(exchangeRateDtoList);

        // Act & Assert
        mockMvc.perform(get("/api/exchange")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(exchangeRateDtoList.size())))
                .andExpect(jsonPath("$[0].title", is("USD")))
                .andExpect(jsonPath("$[0].fullname", is("ДОЛЛАР США")))
                .andExpect(jsonPath("$[0].description", is(449.98)))
                .andExpect(jsonPath("$[1].title", is("EUR")))
                .andExpect(jsonPath("$[1].fullname", is("ЕВРО")))
                .andExpect(jsonPath("$[1].description", is(487.0)))
                .andExpect(jsonPath("$[2].title", is("RUB")))
                .andExpect(jsonPath("$[2].fullname", is("РОССИЙСКИЙ РУБЛЬ")))
                .andExpect(jsonPath("$[2].description", is(5.02)));

        // Verify that the service method was called
        verify(exchangeService, times(1)).getAllExchangeRateDtoByCurrentDate();
    }

    // Method for create object of ExchangeRateDto
    private ExchangeRateDto createExchangeRateDto(String title, String fullname, double description) {
        ExchangeRateDto exchangeRateDto = new ExchangeRateDto();
        exchangeRateDto.setTitle(title);
        exchangeRateDto.setFullname(fullname);
        exchangeRateDto.setDescription(description);
        return exchangeRateDto;
    }
}

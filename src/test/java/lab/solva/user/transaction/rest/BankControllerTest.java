package lab.solva.user.transaction.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import lab.solva.user.transaction.dto.ExpenseTransactionDto;
import lab.solva.user.transaction.service.BankService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SuppressWarnings("unused")
public class BankControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankService bankService;

    @Test
    public void testSaveExpenseTransaction() throws Exception {

        /* Arrange */
        ExpenseTransactionDto expenseTransactionDto = new ExpenseTransactionDto();

        expenseTransactionDto.account_from = "0000000001";
        expenseTransactionDto.account_to = "9000000000";
        expenseTransactionDto.currency_shortname = "USD";
        expenseTransactionDto.sum = 100.0;
        expenseTransactionDto.expense_category = "Service";
        expenseTransactionDto.datetime = "2024-02-01T15:15:20+06:00";

        /* Act & Assert */
        mockMvc.perform(post("/api/bank")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(expenseTransactionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account_from", is("0000000001")))
                .andExpect(jsonPath("$.account_to", is("9000000000")))
                .andExpect(jsonPath("$.currency_shortname", is("USD")))
                .andExpect(jsonPath("$.Sum", is(100.0)))
                .andExpect(jsonPath("$.expense_category", is("Service")))
                // 2024-02-01T15:15:20+06:00 identical to 2024-02-01T09:15:20Z
                .andExpect(jsonPath("$.datetime", is("2024-02-01T09:15:20Z")));

        // Verify that the service method was called
        verify(bankService, times(1)).
                saveExpenseTransactionDto(ArgumentMatchers.any(ExpenseTransactionDto.class));
    }

    // Method to convert an object to a JSON string
    private static String asJsonString(final Object obj) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();

            // Set the desired time format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
                    .withZone(ZoneId.systemDefault());

            objectMapper.registerModule(new JavaTimeModule().addSerializer(ZonedDateTime.class,
                    new ZonedDateTimeSerializer(formatter)));

            // Disable serialization of dates as timestamps
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

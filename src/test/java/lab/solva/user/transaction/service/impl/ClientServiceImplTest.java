///*
//package lab.solva.user.transaction.service.impl;
//
//import lab.solva.user.transaction.dto.AmountLimitDateDto;
//import lab.solva.user.transaction.dto.AmountLimitDto;
//import lab.solva.user.transaction.dto.TransactionExceededLimitDto;
//import lab.solva.user.transaction.model.AmountLimitEntity;
//import lab.solva.user.transaction.repository.AmountLimitRepository;
//import lab.solva.user.transaction.repository.ExpenseTransactionRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class ClientServiceImplTest {
//
//    @Mock
//    private AmountLimitRepository amountLimitRepository;
//
//    @Mock
//    private ExpenseTransactionRepository expenseTransactionRepository;
//
//    @InjectMocks
//    private ClientServiceImpl clientService;
//
//    @Test
//    public void testGetAllAmountLimitDateDtoByAccountClient() {
//
//        /* Arrange */
//        String accountClient = "sampleAccountClient";
//        List<AmountLimitDateDto> expectedDtoList = createSampleDtoList(); // create a method to generate sample data
//
//        when(amountLimitRepository.getAllAmountLimitDateDtoByAccountClient(accountClient))
//                .thenReturn(expectedDtoList);
//
//        /* Act */
//        List<AmountLimitDateDto> actualDtoList = clientService.getAllAmountLimitDateDtoByAccountClient(accountClient);
//
//        /* Assert */
//        assertEquals(expectedDtoList, actualDtoList);
//        verify(amountLimitRepository, times(1)).getAllAmountLimitDateDtoByAccountClient(accountClient);
//    }
//
//    @Test
//    public void testSetAmountLimitDto() {
//
//        /* Arrange */
//        AmountLimitDto amountLimitDto = createSampleAmountLimitDto(); // create a method to generate sample data
//
//        /* Act */
//        clientService.setAmountLimitDto(amountLimitDto);
//
//        /* Assert */
//        verify(amountLimitRepository, times(1)).save(any(AmountLimitEntity.class));
//    }
//
//    @Test
//    public void testGetAllTransactionExceededLimitDtoByAccountClient() {
//
//        /* Arrange */
//        String accountFrom = "sampleAccountFrom";
//        List<TransactionExceededLimitDto> expectedDtoList = createSampleTransactionExceededLimitDtoList(); // create a method to generate sample data
//
//        when(expenseTransactionRepository.getAllTransactionExceededLimitDtoByAccountClient(accountFrom))
//                .thenReturn(expectedDtoList);
//
//        /* Act */
//        List<TransactionExceededLimitDto> actualDtoList = clientService.getAllTransactionExceededLimitDtoByAccountClient(accountFrom);
//
//        /* Assert */
//        assertEquals(expectedDtoList, actualDtoList);
//        verify(expenseTransactionRepository, times(1)).getAllTransactionExceededLimitDtoByAccountClient(accountFrom);
//    }
//
//    // Create methods to generate sample data if needed
//}
//*/
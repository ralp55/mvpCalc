package Service;

import com.example.demo.DTO.LoanOfferDto;
import com.example.demo.DTO.LoanStatementRequestDto;
import com.example.demo.Service.LoanCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LoanCalculatorServiceTest {

    private LoanCalculatorService service;

    @Mock
    private LoanStatementRequestDto mockRequest;

    @BeforeEach
    public void setUp() {
        service = new LoanCalculatorService();
    }

    private LoanStatementRequestDto createValidRequest() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setAmount(new BigDecimal("10000"));
        request.setTerm(12);
        request.setFirstName("Ivan");
        request.setLastName("Ivanov");
        request.setMiddleName("Ivanovich");
        request.setEmail("ivan@example.com");
        request.setBirthdate(LocalDate.of(1990, 1, 1));
        request.setPassportSeries("1234");
        request.setPassportNumber("123456");
        return request;
    }

    @Test
    public void testProcessLoanRequest_validData_returnsOffers() {
        LoanStatementRequestDto request = createValidRequest();

        List<LoanOfferDto> offers = service.processLoanRequest(request);

        assertThat(offers).hasSize(4);
    }

    @Test
    public void testInvalidAmount_throwsException() {
        when(mockRequest.getAmount()).thenReturn(BigDecimal.ZERO);
        when(mockRequest.getTerm()).thenReturn(12);
        when(mockRequest.getFirstName()).thenReturn("Ivan");
        when(mockRequest.getLastName()).thenReturn("Ivanov");
        when(mockRequest.getMiddleName()).thenReturn("Ivanovich");
        when(mockRequest.getEmail()).thenReturn("ivan@example.com");
        when(mockRequest.getBirthdate()).thenReturn(LocalDate.of(1990, 1, 1));
        when(mockRequest.getPassportSeries()).thenReturn("1234");
        when(mockRequest.getPassportNumber()).thenReturn("123456");

        assertThrows(IllegalArgumentException.class, () -> service.processLoanRequest(mockRequest));
    }

    @Test
    public void testInvalidEmail_throwsException() {
        when(mockRequest.getAmount()).thenReturn(new BigDecimal("10000"));
        when(mockRequest.getTerm()).thenReturn(12);
        when(mockRequest.getFirstName()).thenReturn("Ivan");
        when(mockRequest.getLastName()).thenReturn("Ivanov");
        when(mockRequest.getMiddleName()).thenReturn("Ivanovich");
        when(mockRequest.getEmail()).thenReturn("invalid_email");
        when(mockRequest.getBirthdate()).thenReturn(LocalDate.of(1990, 1, 1));
        when(mockRequest.getPassportSeries()).thenReturn("1234");
        when(mockRequest.getPassportNumber()).thenReturn("123456");

        assertThrows(IllegalArgumentException.class, () -> service.processLoanRequest(mockRequest));
    }

    @Test
    public void testUnderageClient_throwsException() {
        LoanStatementRequestDto request = createValidRequest();
        request.setBirthdate(LocalDate.now().minusYears(17));

        assertThrows(IllegalArgumentException.class, () -> service.processLoanRequest(request));
    }

    @Test
    public void testInvalidPassportSeries_throwsException() {
        LoanStatementRequestDto request = createValidRequest();
        request.setPassportSeries("abcd");

        assertThrows(IllegalArgumentException.class, () -> service.processLoanRequest(request));
    }

    @Test
    public void testInvalidPassportNumber_throwsException() {
        LoanStatementRequestDto request = createValidRequest();
        request.setPassportNumber("12AB56");

        assertThrows(IllegalArgumentException.class, () -> service.processLoanRequest(request));
    }

    @Test
    public void testTooShortFirstName_throwsException() {
        LoanStatementRequestDto request = createValidRequest();
        request.setFirstName("I");

        assertThrows(IllegalArgumentException.class, () -> service.processLoanRequest(request));
    }

    @Test
    public void testMissingMiddleName_throwsException() {
        LoanStatementRequestDto request = createValidRequest();
        request.setMiddleName(null);

        assertThrows(IllegalArgumentException.class, () -> service.processLoanRequest(request));
    }
}

package Controller;

import com.example.demo.Controller.CalculatorController;
import com.example.demo.DTO.LoanOfferDto;
import com.example.demo.DTO.LoanStatementRequestDto;
import com.example.demo.DemoApplication;
import com.example.demo.Service.LoanCalculatorService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CalculatorController.class)
@ContextConfiguration(classes = DemoApplication.class)
class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanCalculatorService calculatorService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testProcessLoan_ReturnsLoanOfferList() throws Exception {
        LoanStatementRequestDto requestDto = new LoanStatementRequestDto();
        requestDto.setAmount(BigDecimal.valueOf(100000));
        requestDto.setTerm(12);
        requestDto.setFirstName("Ivan");
        requestDto.setLastName("Ivanov");

        LoanOfferDto offerDto = new LoanOfferDto();
        offerDto.setRequestedAmount(BigDecimal.valueOf(100000));
        offerDto.setTotalAmount(BigDecimal.valueOf(120000));
        offerDto.setRate(BigDecimal.valueOf(10));

        List<LoanOfferDto> offers = Collections.singletonList(offerDto);

        when(calculatorService.processLoanRequest(any(LoanStatementRequestDto.class))).thenReturn(offers);

        mockMvc.perform(post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].requestedAmount").value(100000))
                .andExpect(jsonPath("$[0].totalAmount").value(120000))
                .andExpect(jsonPath("$[0].rate").value(10));

        verify(calculatorService, times(1)).processLoanRequest(any(LoanStatementRequestDto.class));
    }
}

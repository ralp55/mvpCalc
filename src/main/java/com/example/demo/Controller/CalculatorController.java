package com.example.demo.Controller;


import com.example.demo.DTO.LoanOfferDto;
import com.example.demo.DTO.LoanStatementRequestDto;
import com.example.demo.Service.LoanCalculatorService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/calculator")
@Tag(name = "Loan Calculator API", description = "API для расчёта возможности кредитации")
public class CalculatorController {

    @Autowired
    private LoanCalculatorService calculatorService;
    @Operation(
            summary = "Получение кредитных предложений",
            description = "Вычисляет список возможных кредитных предложений по параметрам клиента.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный ответ",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = LoanOfferDto.class)))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректные входные данные",
                            content = @Content
                    )
            }
    )
    @PostMapping("/offers")
    public List<LoanOfferDto> processLoan(@RequestBody LoanStatementRequestDto request) {
        return calculatorService.processLoanRequest(request);
    }
}

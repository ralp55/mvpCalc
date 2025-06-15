package com.example.demo.Controller;

import com.example.demo.DTO.CreditDto;
import com.example.demo.DTO.LoanOfferDto;
import com.example.demo.DTO.ScoringDataDto;
import com.example.demo.Service.CreditCalculationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/calculator")
@Tag(name = "Credit Calculator API", description = "API для расчёта кредитных условий")
public class CreditController {

    private final CreditCalculationService calculationService;

    public CreditController(CreditCalculationService calculationService) {
        this.calculationService = calculationService;
    }
    @Operation(
            summary = "Рассчитать кредитные предложения",
            description = "Принимает данные запроса на кредит и возвращает список кредитных предложений с различными условиями",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список кредитных предложений",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LoanOfferDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректные входные данные",
                            content = @Content)
            }
    )
    @PostMapping("/calc")
    public CreditDto calculate(@RequestBody ScoringDataDto request) {
        return calculationService.calculateCredit(request);
    }
}

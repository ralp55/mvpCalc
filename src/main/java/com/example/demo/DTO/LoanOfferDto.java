package com.example.demo.DTO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;



@Data
@Schema(description = "Кредитное предложение")
public class LoanOfferDto {

    @Schema(description = "ID заявления", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID statementId;

    @Schema(description = "Запрошенная сумма", example = "100000")
    private BigDecimal requestedAmount;

    @Schema(description = "Общая сумма к возврату", example = "105000")
    private BigDecimal totalAmount;

    @Schema(description = "Срок кредита (месяцы)", example = "12")
    private Integer term;

    @Schema(description = "Ежемесячный платёж", example = "8750.00")
    private BigDecimal monthlyPayment;

    @Schema(description = "Процентная ставка", example = "9.5")
    private BigDecimal rate;

    @Schema(description = "Включено страхование", example = "true")
    private Boolean isInsuranceEnabled;

    @Schema(description = "Является зарплатным клиентом", example = "false")
    private Boolean isSalaryClient;


}

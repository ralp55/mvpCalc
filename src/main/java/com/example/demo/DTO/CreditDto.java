package com.example.demo.DTO;

import java.math.BigDecimal;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CreditDto {
    @Schema(description = "Сумма кредита", example = "500000.00")
    private BigDecimal amount;

    @Schema(description = "Срок кредита (в месяцах)", example = "12")
    private Integer term;

    @Schema(description = "Ежемесячный платёж", example = "45000.00")
    private BigDecimal monthlyPayment;

    @Schema(description = "Процентная ставка", example = "9.5")
    private BigDecimal rate;

    @Schema(description = "Полная стоимость кредита (ПСК)", example = "512000.00")
    private BigDecimal psk;

    @Schema(description = "Страховка подключена", example = "true")
    private Boolean isInsuranceEnabled;

    @Schema(description = "Зарплатный клиент", example = "false")
    private Boolean isSalaryClient;

    @Schema(description = "График платежей")
    private List<PaymentScheduleElementDto> paymentSchedule;
}

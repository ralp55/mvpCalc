package com.example.demo.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
@Data
public class PaymentScheduleElementDto {
    @Schema(description = "Номер платежа", example = "1")
    private Integer number;

    @Schema(description = "Дата платежа", example = "2025-07-01")
    private LocalDate date;

    @Schema(description = "Общая сумма платежа", example = "45000.00")
    private BigDecimal totalPayment;

    @Schema(description = "Процент по кредиту", example = "5000.00")
    private BigDecimal interestPayment;

    @Schema(description = "Погашение основного долга", example = "40000.00")
    private BigDecimal debtPayment;

    @Schema(description = "Оставшийся долг", example = "460000.00")
    private BigDecimal remainingDebt;
}

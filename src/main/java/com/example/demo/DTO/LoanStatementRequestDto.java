package com.example.demo.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Данные запроса на кредит")
public class LoanStatementRequestDto {
    @Schema(description = "Запрашиваемая сумма кредита", example = "100000")
    private BigDecimal amount;

    @Schema(description = "Срок кредита в месяцах", example = "12")
    private Integer term;

    @Schema(description = "Имя", example = "Иван")
    private String firstName;

    @Schema(description = "Фамилия", example = "Иванов")
    private String lastName;

    @Schema(description = "Отчество", example = "Иванович")
    private String middleName;

    @Schema(description = "Email", example = "ivanov@example.com")
    private String email;

    @Schema(description = "Дата рождения", example = "1985-06-15")
    private LocalDate birthdate;

    @Schema(description = "Серия паспорта", example = "1234")
    private String passportSeries;

    @Schema(description = "Номер паспорта", example = "567890")
    private String passportNumber;

}

package com.example.demo.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ScoringDataDto {
    @Schema(description = "Сумма кредита", example = "500000")
    private BigDecimal amount;

    @Schema(description = "Срок кредита (в месяцах)", example = "12")
    private Integer term;

    @Schema(description = "Имя", example = "Иван")
    private String firstName;

    @Schema(description = "Фамилия", example = "Иванов")
    private String lastName;

    @Schema(description = "Отчество", example = "Иванович")
    private String middleName;

    @Schema(description = "Пол", example = "MALE")
    private Gender gender;

    @Schema(description = "Дата рождения", example = "1990-01-01")
    private LocalDate birthdate;

    @Schema(description = "Серия паспорта", example = "1234")
    private String passportSeries;

    @Schema(description = "Номер паспорта", example = "567890")
    private String passportNumber;

    @Schema(description = "Дата выдачи паспорта", example = "2010-05-15")
    private LocalDate passportIssueDate;

    @Schema(description = "Кем выдан паспорт", example = "ОВД Центрального района")
    private String passportIssueBranch;

    @Schema(description = "Семейное положение", example = "MARRIED")
    private MaritalStatus maritalStatus;

    @Schema(description = "Количество иждивенцев", example = "2")
    private Integer dependentAmount;

    @Schema(description = "Информация о занятости")
    private EmploymentDto employment;

    @Schema(description = "Номер счёта клиента", example = "40817810099910004312")
    private String accountNumber;

    @Schema(description = "Подключена ли страховка", example = "true")
    private Boolean isInsuranceEnabled;

    @Schema(description = "Является ли зарплатным клиентом", example = "false")
    private Boolean isSalaryClient;
}


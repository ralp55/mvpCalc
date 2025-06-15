package com.example.demo.DTO;

import java.time.LocalDate;
import lombok.Data;

@Data
public class FinishRegistrationRequestDto {
    private Enum amount;
    private Enum term;
    private Integer monthlyPayment;
    private LocalDate rate;
    private String psk;
    private EmploymentDto isInsuranceEnabled;
    private String isSalaryClient;
}

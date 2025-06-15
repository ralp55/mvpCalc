package com.example.demo.Service;

import com.example.demo.DTO.*;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CreditCalculationService {

    private static final Logger logger = LoggerFactory.getLogger(CreditCalculationService.class);

    public CreditDto calculateCredit(ScoringDataDto scoring) {
        logger.info("Starting credit calculation for client: {} {} {}",
                scoring.getLastName(), scoring.getFirstName(), scoring.getMiddleName());
        logger.debug("ScoringDataDto received: {}", scoring);
        validateRequest(scoring);
        BigDecimal amount = scoring.getAmount();
        int term = scoring.getTerm();
        boolean insurance = Boolean.TRUE.equals(scoring.getIsInsuranceEnabled());
        boolean salary = Boolean.TRUE.equals(scoring.getIsSalaryClient());

        BigDecimal baseRate = new BigDecimal("10.0");

        if (insurance) baseRate = baseRate.subtract(new BigDecimal("1.0"));
        if (salary) baseRate = baseRate.subtract(new BigDecimal("0.5"));

        BigDecimal scoringDelta = applyScoringAdjustments(scoring);
        if (scoringDelta == null) return null;

        baseRate = baseRate.add(scoringDelta);
        logger.debug("scoringDelta: {}, baseRate: {}", scoringDelta, baseRate);
        BigDecimal monthlyRate = baseRate.divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_UP);
        logger.debug("monthlyRate: {}", monthlyRate);
        BigDecimal monthlyPayment = calculateMonthlyPayment(amount, monthlyRate, term);
        logger.debug("monthlyPayment: {}", monthlyPayment);
        List<PaymentScheduleElementDto> schedule = buildSchedule(amount, term, monthlyRate, monthlyPayment);

        BigDecimal totalPayments = monthlyPayment.multiply(BigDecimal.valueOf(term));
        if (insurance) {
            BigDecimal insuranceCost = amount.multiply(new BigDecimal("0.01"));
            totalPayments = totalPayments.add(insuranceCost);
        }

        BigDecimal psk = totalPayments.divide(amount, 10, RoundingMode.HALF_UP)
                .subtract(BigDecimal.ONE)
                .multiply(BigDecimal.valueOf(100));
        logger.info("Total payments: {}, PSK: {}", totalPayments, psk);
        CreditDto credit = new CreditDto();
        credit.setAmount(amount);
        credit.setTerm(term);
        credit.setRate(baseRate.setScale(2, RoundingMode.HALF_UP));
        credit.setMonthlyPayment(monthlyPayment.setScale(2, RoundingMode.HALF_UP));
        credit.setPsk(psk.setScale(2, RoundingMode.HALF_UP));
        credit.setIsInsuranceEnabled(insurance);
        credit.setIsSalaryClient(salary);
        credit.setPaymentSchedule(schedule);
        logger.info("Credit info: {}", credit);
        return credit;
    }
    private BigDecimal applyScoringAdjustments(ScoringDataDto dto) {
        BigDecimal delta = BigDecimal.ZERO;

        LocalDate now = LocalDate.now();
        int age = now.getYear() - dto.getBirthdate().getYear();


        if (age < 20 || age > 65) {
            return null;
        }

        EmploymentDto emp = dto.getEmployment();
        if (emp == null || emp.getEmploymentStatus() == EmploymentStatus.UNEMPLOYED) {
            return null;
        } else if (emp.getEmploymentStatus() == EmploymentStatus.BUSINESS_OWNER) {
            delta = delta.add(new BigDecimal("1.0"));
        }
        else if (emp.getEmploymentStatus() == EmploymentStatus.SELF_EMPLOYED) {
            delta = delta.add(new BigDecimal("2.0"));
        }

        if (emp.getSalary() != null && emp.getSalary().multiply(BigDecimal.valueOf(24)).compareTo(dto.getAmount()) < 0) {
            return null;
        }

        if (emp.getWorkExperienceCurrent() != null && emp.getWorkExperienceCurrent() < 3) {
            delta = delta.add(new BigDecimal("1.0"));
        }

        if (emp.getWorkExperienceTotal() != null && emp.getWorkExperienceTotal() < 18) {
            delta = delta.add(new BigDecimal("2.0"));
        }

        if (dto.getDependentAmount() != null && dto.getDependentAmount() > 3) {
            delta = delta.add(new BigDecimal("0.5"));
        }

        if (dto.getMaritalStatus() == MaritalStatus.MARRIED) {
            delta = delta.subtract(new BigDecimal("3.0"));
        } else if (dto.getMaritalStatus() == MaritalStatus.NON_MARRIED) {
            delta = delta.add(new BigDecimal("1.0"));
        }

        if ((dto.getGender() == Gender.MALE && age < 55) || (dto.getGender() == Gender.FEMALE && age < 60) ||
                (dto.getGender() == Gender.MALE && age > 30) || (dto.getGender() == Gender.FEMALE && age > 32)) {
            delta = delta.subtract(new BigDecimal("3.0"));
        }

        return delta;
    }
    private void validateRequest(ScoringDataDto req) {
        if (req.getAmount() == null || req.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be a positive number");
        }

        if (req.getTerm() == null || req.getTerm() <= 0) {
            throw new IllegalArgumentException("Term must be a positive number");
        }

        if (req.getFirstName() == null || req.getFirstName().length() < 2 || req.getFirstName().length() > 30) {
            throw new IllegalArgumentException("First name must be between 2 and 30 characters");
        }

        if (!req.getFirstName().matches("^[A-Za-zА-Яа-яЁё\\-]+$")) {
            throw new IllegalArgumentException("First name contains invalid characters");
        }

        if (req.getLastName() == null || req.getLastName().length() < 2 || req.getLastName().length() > 30) {
            throw new IllegalArgumentException("Last name must be between 2 and 30 characters");
        }

        if (!req.getLastName().matches("^[A-Za-zА-Яа-яЁё\\-]+$")) {
            throw new IllegalArgumentException("Last name contains invalid characters");
        }

        if (req.getMiddleName() == null || req.getMiddleName().length() < 2 || req.getMiddleName().length() > 30) {
            throw new IllegalArgumentException("Middle name must be between 2 and 30 characters");
        }

        if (!req.getMiddleName().matches("^[A-Za-zА-Яа-яЁё\\-]+$")) {
            throw new IllegalArgumentException("Middle name contains invalid characters");
        }

        if (req.getBirthdate() == null) {
            throw new IllegalArgumentException("Birthdate is required");
        }

        LocalDate birthdate = req.getBirthdate();
        int age = Period.between(birthdate, LocalDate.now()).getYears();
        if (age < 20 || age > 65) {
            throw new IllegalArgumentException("Age must be between 20 and 65 years");
        }

        if (req.getPassportSeries() == null || !req.getPassportSeries().matches("\\d{4}")) {
            throw new IllegalArgumentException("Passport series must be exactly 4 digits");
        }

        if (req.getPassportNumber() == null || !req.getPassportNumber().matches("\\d{6}")) {
            throw new IllegalArgumentException("Passport number must be 6 digits");
        }

        EmploymentDto emp = req.getEmployment();
        if (emp == null || emp.getEmploymentStatus() == EmploymentStatus.UNEMPLOYED) {
            throw new IllegalArgumentException("Employment status is required and must not be UNEMPLOYED");
        }

        if (emp.getSalary() == null) {
            throw new IllegalArgumentException("Salary must not be null");
        }

        BigDecimal minSalary = req.getAmount().divide(BigDecimal.valueOf(24), RoundingMode.HALF_UP);
        if (emp.getSalary().compareTo(minSalary) < 0) {
            throw new IllegalArgumentException("Salary too low for requested amount");
        }
    }



    private BigDecimal calculateMonthlyPayment(BigDecimal amount, BigDecimal monthlyRate, int term) {
        BigDecimal onePlusR = monthlyRate.add(BigDecimal.ONE);
        BigDecimal pow = onePlusR.pow(-term, new MathContext(10));
        return amount.multiply(monthlyRate).divide(BigDecimal.ONE.subtract(pow), RoundingMode.HALF_UP);
    }

    private List<PaymentScheduleElementDto> buildSchedule(BigDecimal amount, int term, BigDecimal rate, BigDecimal monthlyPayment) {
        List<PaymentScheduleElementDto> list = new ArrayList<>();
        BigDecimal remaining = amount;
        LocalDate date = LocalDate.now();

        for (int i = 1; i <= term; i++) {

            BigDecimal interest = remaining.multiply(rate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal debt = monthlyPayment.subtract(interest).setScale(2, RoundingMode.HALF_UP);
            remaining = remaining.subtract(debt).setScale(2, RoundingMode.HALF_UP);

            PaymentScheduleElementDto elem = new PaymentScheduleElementDto();
            elem.setNumber(i);
            elem.setDate(date.plusMonths(i));
            elem.setTotalPayment(monthlyPayment.setScale(2, RoundingMode.HALF_UP));
            elem.setInterestPayment(interest);
            elem.setDebtPayment(debt);
            elem.setRemainingDebt(remaining.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : remaining);

            list.add(elem);
        }

        return list;
    }
}

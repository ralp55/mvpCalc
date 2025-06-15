package com.example.demo.Service;

import com.example.demo.DTO.LoanStatementRequestDto;
import com.example.demo.DTO.LoanOfferDto;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
public class LoanCalculatorService {

    private static final Logger logger = LoggerFactory.getLogger(LoanCalculatorService.class);

    public List<LoanOfferDto> processLoanRequest(LoanStatementRequestDto request) {
        logger.info("Received loan request: {}", request);

        validateRequest(request);

        logger.debug("Request validated successfully");
        List<LoanOfferDto> offers = new ArrayList<>();
        UUID statementId = UUID.randomUUID();

        for (boolean insurance : List.of(true, false)) {
            for (boolean salary : List.of(true, false)) {

                BigDecimal rate = calculateRate(insurance, salary);
                BigDecimal monthlyPayment = calculateMonthlyPayment(request.getAmount(), rate, request.getTerm());
                BigDecimal totalAmount = monthlyPayment.multiply(BigDecimal.valueOf(request.getTerm()));

                logger.debug("Combination: insurance={}, salary={}, rate={}, monthlyPayment={}, totalAmount={}",
                        insurance, salary, rate, monthlyPayment, totalAmount);

                LoanOfferDto offer = new LoanOfferDto();
                offer.setStatementId(statementId);
                offer.setRequestedAmount(request.getAmount());
                offer.setTotalAmount(totalAmount.setScale(2, RoundingMode.HALF_UP));
                offer.setTerm(request.getTerm());
                offer.setMonthlyPayment(monthlyPayment.setScale(2, RoundingMode.HALF_UP));
                offer.setRate(rate);
                offer.setIsInsuranceEnabled(insurance);
                offer.setIsSalaryClient(salary);

                offers.add(offer);
            }
        }
        offers.sort(Comparator.comparing(LoanOfferDto::getRate));

        logger.info("Generated {} loan offers for statementId={}", offers.size(), statementId);

        return offers;
    }

    private void validateRequest(LoanStatementRequestDto req) {
        logger.debug("Validation start");
        if (req.getAmount() == null || req.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be positive");

        if (req.getTerm() == null || req.getTerm() <= 0)
            throw new IllegalArgumentException("Term must be positive");

        if (req.getFirstName().length() < 2 ||req.getFirstName().length() >30)
            throw new IllegalArgumentException("First name is required");

        if (req.getLastName().length()<2 || req.getLastName().length()>30)
            throw new IllegalArgumentException("Last name is required");

        if (req.getMiddleName() == null || req.getMiddleName().length() < 2 || req.getMiddleName().length() > 30)
            throw new IllegalArgumentException("Middle name is required");

        if (req.getEmail() == null || !req.getEmail().matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"))
            throw new IllegalArgumentException("Invalid email format");

        if (req.getBirthdate() == null || req.getBirthdate().isAfter(LocalDate.now().minusYears(18)))
            throw new IllegalArgumentException("User must be at least 18 years old");

        if (req.getPassportSeries() == null || !req.getPassportSeries().matches("\\d{4}"))
            throw new IllegalArgumentException("Passport series must be 4 digits");

        if (req.getPassportNumber() == null || !req.getPassportNumber().matches("\\d{6}"))
            throw new IllegalArgumentException("Passport number must be 6 digits");
        logger.debug("Validation end");
    }

    private BigDecimal calculateRate(boolean insurance, boolean salary) {
        logger.debug("calculateRate start");
        BigDecimal baseRate = new BigDecimal("10.0");
        if (insurance) baseRate = baseRate.subtract(new BigDecimal("1.0"));
        if (salary) baseRate = baseRate.subtract(new BigDecimal("0.5"));
        logger.debug("calculateRate info: baseRate={}, salary={}, baseRate={}",
                insurance, salary, baseRate);
        return baseRate;
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal principal, BigDecimal rateAnnual, int termMonths) {
        logger.debug("calculateMonthlyPayment start");
        BigDecimal monthlyRate = rateAnnual.divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_UP);
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(termMonths), 10, RoundingMode.HALF_UP);
        }
        BigDecimal onePlusRPowerN = monthlyRate.add(BigDecimal.ONE).pow(termMonths);
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(onePlusRPowerN);
        BigDecimal denominator = onePlusRPowerN.subtract(BigDecimal.ONE);
        BigDecimal answer = numerator.divide(denominator, 10, RoundingMode.HALF_UP);
        logger.debug("calculateMonthlyPayment info: MonthlyPayment={}", answer);
        return answer;
    }
}


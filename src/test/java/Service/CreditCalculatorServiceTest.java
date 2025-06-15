package Service;

import com.example.demo.DTO.*;
import com.example.demo.Service.CreditCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditCalculatorServiceTest {

    private CreditCalculationService service;

    @Mock
    private ScoringDataDto scoringMock;

    @Mock
    private EmploymentDto employmentMock;

    @BeforeEach
    void setUp() {
        service = new CreditCalculationService();
    }

    @Test
    void testAmountIsNull() {
        when(scoringMock.getAmount()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoringMock));
    }
    @Test
    void testAmountIsnotNull() {
        when(scoringMock.getAmount()).thenReturn(BigDecimal.valueOf(1));
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoringMock));
    }

    @Test
    void testAmountIsNegative() {
        when(scoringMock.getAmount()).thenReturn(BigDecimal.valueOf(-1));
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoringMock));
    }

    @Test
    void testTermNullOrNonPositive() {
        when(scoringMock.getAmount()).thenReturn(BigDecimal.valueOf(100000));
        when(scoringMock.getTerm()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoringMock));

        when(scoringMock.getTerm()).thenReturn(0);
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoringMock));
    }

    @Test
    void testInvalidFirstName() {
        when(scoringMock.getAmount()).thenReturn(BigDecimal.valueOf(100000));
        when(scoringMock.getTerm()).thenReturn(12);
        when(scoringMock.getFirstName()).thenReturn("A");
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoringMock));

        when(scoringMock.getFirstName()).thenReturn("VeryLongNameOverThirtyCharacters123");
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoringMock));

        when(scoringMock.getFirstName()).thenReturn("Iv@n");
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoringMock));
    }

    @Test
    void testInvalidBirthdate() {
        when(scoringMock.getAmount()).thenReturn(BigDecimal.valueOf(100000));
        when(scoringMock.getTerm()).thenReturn(12);
        when(scoringMock.getFirstName()).thenReturn("Ivan");
        when(scoringMock.getLastName()).thenReturn("Petrov");
        when(scoringMock.getMiddleName()).thenReturn("Ivanovich");
        when(scoringMock.getBirthdate()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoringMock));

        when(scoringMock.getBirthdate()).thenReturn(LocalDate.now().minusYears(17));
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoringMock));

        when(scoringMock.getBirthdate()).thenReturn(LocalDate.now().minusYears(70));
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoringMock));
    }

    @Test
    void testInvalidPassportSeries() {
        when(scoringMock.getAmount()).thenReturn(BigDecimal.valueOf(100000));
        when(scoringMock.getTerm()).thenReturn(12);
        when(scoringMock.getFirstName()).thenReturn("Ivan");
        when(scoringMock.getLastName()).thenReturn("Petrov");
        when(scoringMock.getMiddleName()).thenReturn("Ivanovich");
        when(scoringMock.getBirthdate()).thenReturn(LocalDate.of(1990, 1, 1));
        when(scoringMock.getPassportSeries()).thenReturn("12A4");

        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoringMock));
    }

    @Test
    void testInvalidEmploymentStatus() {
        when(scoringMock.getAmount()).thenReturn(BigDecimal.valueOf(100000));
        when(scoringMock.getTerm()).thenReturn(12);
        when(scoringMock.getFirstName()).thenReturn("Ivan");
        when(scoringMock.getLastName()).thenReturn("Petrov");
        when(scoringMock.getMiddleName()).thenReturn("Ivanovich");
        when(scoringMock.getBirthdate()).thenReturn(LocalDate.of(1990, 1, 1));
        when(scoringMock.getPassportSeries()).thenReturn("1234");
        when(scoringMock.getPassportNumber()).thenReturn("123456");
        when(scoringMock.getEmployment()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoringMock));

        when(scoringMock.getEmployment()).thenReturn(employmentMock);
        when(employmentMock.getEmploymentStatus()).thenReturn(EmploymentStatus.UNEMPLOYED);
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoringMock));
    }
    @Test
    void testSalaryTooLowOrUnemployedThrows() {

        when(scoringMock.getAmount()).thenReturn(BigDecimal.valueOf(500000));
        when(scoringMock.getTerm()).thenReturn(12);
        when(scoringMock.getBirthdate()).thenReturn(LocalDate.of(1990, 1, 1));
        when(scoringMock.getFirstName()).thenReturn("Ivan");
        when(scoringMock.getLastName()).thenReturn("Petrov");
        when(scoringMock.getMiddleName()).thenReturn("Ivanovich");
        when(scoringMock.getPassportSeries()).thenReturn("1234");
        when(scoringMock.getPassportNumber()).thenReturn("123456");

        when(scoringMock.getEmployment()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCredit(scoringMock);
        });

        when(scoringMock.getEmployment()).thenReturn(employmentMock);
        when(employmentMock.getEmploymentStatus()).thenReturn(EmploymentStatus.UNEMPLOYED);
        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCredit(scoringMock);
        });

        when(employmentMock.getEmploymentStatus()).thenReturn(EmploymentStatus.EMPLOYED);
        when(employmentMock.getSalary()).thenReturn(BigDecimal.valueOf(10000));
        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCredit(scoringMock);
        });

        when(employmentMock.getSalary()).thenReturn(BigDecimal.valueOf(30000));
        CreditDto result = service.calculateCredit(scoringMock);
        assertNotNull(result);
    }


    @Test
    void testNullLastNameThrows() {
        lenient().when(scoringMock.getLastName()).thenReturn(null);
        lenient().when(scoringMock.getMiddleName()).thenReturn("Ivanovich");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCredit(scoringMock);
        });
        assertFalse(ex.getMessage().contains("Last name"));
    }

    @Test
    void testShortLastNameThrows() {
        lenient().when(scoringMock.getLastName()).thenReturn("A");
        lenient().when(scoringMock.getMiddleName()).thenReturn("Ivanovich");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCredit(scoringMock);
        });
        assertFalse(ex.getMessage().contains("Last name"));
    }

    @Test
    void testNullMiddleNameThrows() {
        lenient().when(scoringMock.getLastName()).thenReturn("Petrov");
        lenient().when(scoringMock.getMiddleName()).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCredit(scoringMock);
        });
        assertFalse(ex.getMessage().contains("Middle name"));
    }

    @Test
    void testShortMiddleNameThrows() {
        lenient().when(scoringMock.getLastName()).thenReturn("Petrov");
        lenient().when(scoringMock.getMiddleName()).thenReturn("I");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCredit(scoringMock);
        });
        assertFalse(ex.getMessage().contains("Middle name"));
    }


    @Test
    void testSalaryTooLow() {
        when(scoringMock.getAmount()).thenReturn(BigDecimal.valueOf(240000));
        when(scoringMock.getTerm()).thenReturn(12);
        when(scoringMock.getFirstName()).thenReturn("Ivan");
        when(scoringMock.getLastName()).thenReturn("Petrov");
        when(scoringMock.getMiddleName()).thenReturn("Ivanovich");
        when(scoringMock.getBirthdate()).thenReturn(LocalDate.of(1990, 1, 1));
        when(scoringMock.getPassportSeries()).thenReturn("1234");
        when(scoringMock.getPassportNumber()).thenReturn("123456");
        when(scoringMock.getEmployment()).thenReturn(employmentMock);
        when(employmentMock.getEmploymentStatus()).thenReturn(EmploymentStatus.EMPLOYED);
        when(employmentMock.getSalary()).thenReturn(BigDecimal.valueOf(9000));

        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoringMock));
    }
    @Test
    void testValidInputReturnsCreditDto() {
        when(scoringMock.getAmount()).thenReturn(BigDecimal.valueOf(240000));
        when(scoringMock.getTerm()).thenReturn(24);
        when(scoringMock.getFirstName()).thenReturn("Ivan");
        when(scoringMock.getLastName()).thenReturn("Petrov");
        when(scoringMock.getMiddleName()).thenReturn("Ivanovich");
        when(scoringMock.getBirthdate()).thenReturn(LocalDate.of(1990, 1, 1));
        when(scoringMock.getPassportSeries()).thenReturn("1234");
        when(scoringMock.getPassportNumber()).thenReturn("123456");
        when(scoringMock.getGender()).thenReturn(Gender.MALE);
        when(scoringMock.getMaritalStatus()).thenReturn(MaritalStatus.MARRIED);
        when(scoringMock.getDependentAmount()).thenReturn(2);
        when(scoringMock.getEmployment()).thenReturn(employmentMock);

        when(employmentMock.getEmploymentStatus()).thenReturn(EmploymentStatus.EMPLOYED);
        when(employmentMock.getSalary()).thenReturn(BigDecimal.valueOf(20000));
        when(employmentMock.getWorkExperienceCurrent()).thenReturn(6);
        when(employmentMock.getWorkExperienceTotal()).thenReturn(36);

        var result = service.calculateCredit(scoringMock);

        assertNotNull(result);
        assertTrue(result.getPsk().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(result.getMonthlyPayment().compareTo(BigDecimal.ZERO) > 0);
        assertEquals(BigDecimal.valueOf(240000), result.getAmount());
    }
    @Test
    void testScoringAdjustmentsBusinessOwner() {
        when(scoringMock.getAmount()).thenReturn(BigDecimal.valueOf(240000));
        when(scoringMock.getTerm()).thenReturn(24);
        when(scoringMock.getFirstName()).thenReturn("Ivan");
        when(scoringMock.getLastName()).thenReturn("Petrov");
        when(scoringMock.getMiddleName()).thenReturn("Ivanovich");
        when(scoringMock.getBirthdate()).thenReturn(LocalDate.of(1990, 1, 1));
        when(scoringMock.getPassportSeries()).thenReturn("1234");
        when(scoringMock.getPassportNumber()).thenReturn("123456");
        when(scoringMock.getGender()).thenReturn(Gender.MALE);
        when(scoringMock.getMaritalStatus()).thenReturn(MaritalStatus.NON_MARRIED);
        when(scoringMock.getDependentAmount()).thenReturn(4);
        when(scoringMock.getEmployment()).thenReturn(employmentMock);

        when(employmentMock.getEmploymentStatus()).thenReturn(EmploymentStatus.BUSINESS_OWNER);
        when(employmentMock.getSalary()).thenReturn(BigDecimal.valueOf(20000));
        when(employmentMock.getWorkExperienceCurrent()).thenReturn(1);
        when(employmentMock.getWorkExperienceTotal()).thenReturn(12);

        var result = service.calculateCredit(scoringMock);

        assertNotNull(result);
        assertTrue(result.getRate().compareTo(BigDecimal.ZERO) > 0);
    }
    @Test
    void testAgeBoundaryValid() {
        LocalDate twentyYearsAgo = LocalDate.now().minusYears(20);
        LocalDate sixtyFiveYearsAgo = LocalDate.now().minusYears(65);

        when(scoringMock.getAmount()).thenReturn(BigDecimal.valueOf(240000));
        when(scoringMock.getTerm()).thenReturn(24);
        when(scoringMock.getFirstName()).thenReturn("Ivan");
        when(scoringMock.getLastName()).thenReturn("Petrov");
        when(scoringMock.getMiddleName()).thenReturn("Ivanovich");
        when(scoringMock.getPassportSeries()).thenReturn("1234");
        when(scoringMock.getPassportNumber()).thenReturn("123456");
        when(scoringMock.getGender()).thenReturn(Gender.MALE);
        when(scoringMock.getMaritalStatus()).thenReturn(MaritalStatus.MARRIED);
        when(scoringMock.getDependentAmount()).thenReturn(1);
        when(scoringMock.getEmployment()).thenReturn(employmentMock);
        when(employmentMock.getEmploymentStatus()).thenReturn(EmploymentStatus.EMPLOYED);
        when(employmentMock.getSalary()).thenReturn(BigDecimal.valueOf(20000));
        when(employmentMock.getWorkExperienceCurrent()).thenReturn(5);
        when(employmentMock.getWorkExperienceTotal()).thenReturn(30);


        when(scoringMock.getBirthdate()).thenReturn(twentyYearsAgo);
        assertDoesNotThrow(() -> service.calculateCredit(scoringMock));


        when(scoringMock.getBirthdate()).thenReturn(sixtyFiveYearsAgo);
        assertDoesNotThrow(() -> service.calculateCredit(scoringMock));
    }
    @Test
    void testSuccessfulCreditCalculation() {

        when(scoringMock.getAmount()).thenReturn(BigDecimal.valueOf(500_000));
        when(scoringMock.getTerm()).thenReturn(12);
        when(scoringMock.getIsInsuranceEnabled()).thenReturn(true);
        when(scoringMock.getIsSalaryClient()).thenReturn(true);
        when(scoringMock.getFirstName()).thenReturn("Ivan");
        when(scoringMock.getLastName()).thenReturn("Ivanov");
        when(scoringMock.getMiddleName()).thenReturn("Ivanovich");
        when(scoringMock.getBirthdate()).thenReturn(LocalDate.of(1990, 1, 1));
        when(scoringMock.getPassportSeries()).thenReturn("1234");
        when(scoringMock.getPassportNumber()).thenReturn("123456");
        when(scoringMock.getGender()).thenReturn(Gender.MALE);
        when(scoringMock.getMaritalStatus()).thenReturn(MaritalStatus.MARRIED);
        when(scoringMock.getDependentAmount()).thenReturn(0);

        when(scoringMock.getEmployment()).thenReturn(employmentMock);
        when(employmentMock.getEmploymentStatus()).thenReturn(EmploymentStatus.EMPLOYED);
        when(employmentMock.getSalary()).thenReturn(BigDecimal.valueOf(150_000));
        when(employmentMock.getWorkExperienceCurrent()).thenReturn(24);
        when(employmentMock.getWorkExperienceTotal()).thenReturn(60);

        CreditDto result = service.calculateCredit(scoringMock);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(500_000), result.getAmount());
        assertEquals(12, result.getTerm());
        assertTrue(result.getIsInsuranceEnabled());
        assertTrue(result.getIsSalaryClient());


        assertEquals(new BigDecimal("2.50"), result.getRate());

        assertNotNull(result.getMonthlyPayment());
        assertNotNull(result.getPsk());

        assertEquals(12, result.getPaymentSchedule().size());
    }



    @Test
    void testFemaleDivorcedGivesRateBonus() {
        when(scoringMock.getGender()).thenReturn(Gender.FEMALE);
        when(scoringMock.getMaritalStatus()).thenReturn(MaritalStatus.NON_MARRIED);
        when(scoringMock.getFirstName()).thenReturn("Ivana");
        when(scoringMock.getLastName()).thenReturn("Petrova");
        when(scoringMock.getMiddleName()).thenReturn("Ivanovna");
        when(scoringMock.getBirthdate()).thenReturn(LocalDate.of(1995, 1, 1));
        when(scoringMock.getAmount()).thenReturn(BigDecimal.valueOf(100000));
        when(scoringMock.getTerm()).thenReturn(12);
        when(scoringMock.getEmployment()).thenReturn(employmentMock);
        when(employmentMock.getEmploymentStatus()).thenReturn(EmploymentStatus.EMPLOYED);
        when(employmentMock.getSalary()).thenReturn(BigDecimal.valueOf(20000));
        when(employmentMock.getWorkExperienceCurrent()).thenReturn(5);
        when(employmentMock.getWorkExperienceTotal()).thenReturn(36);
        when(scoringMock.getPassportSeries()).thenReturn("1234");
        when(scoringMock.getPassportNumber()).thenReturn("123456");
        var result = service.calculateCredit(scoringMock);

        assertNotNull(result);
        assertTrue(result.getRate().compareTo(BigDecimal.ZERO) > 0);
    }
    @Test
    void testNullSalaryThrows() {
        lenient().when(employmentMock.getSalary()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCredit(scoringMock);
        });
    }

    @Test
    void testInvalidPassportNumberThrows() {
        lenient().when(scoringMock.getPassportNumber()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCredit(scoringMock);
        });

        lenient().when(scoringMock.getPassportNumber()).thenReturn("12345");
        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCredit(scoringMock);
        });


        lenient().when(scoringMock.getPassportNumber()).thenReturn("1234567");
        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCredit(scoringMock);
        });

        lenient().when(scoringMock.getPassportNumber()).thenReturn("abc123");
        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCredit(scoringMock);
        });
    }

    @Test
    void testInvalidLastNameThrows() {
        lenient().when(scoringMock.getLastName()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCredit(scoringMock);
        });

        lenient().when(scoringMock.getLastName()).thenReturn("A");
        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCredit(scoringMock);
        });

        lenient().when(scoringMock.getLastName()).thenReturn("ThisNameIsWayTooLongForTheValidationCheck");
        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCredit(scoringMock);
        });

        lenient().when(scoringMock.getLastName()).thenReturn("Petro#v");
        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCredit(scoringMock);
        });
    }

    @Test
    void testInvalidMiddleNameThrows() {
        lenient().when(scoringMock.getMiddleName()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCredit(scoringMock);
        });

        lenient().when(scoringMock.getMiddleName()).thenReturn("I");
        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCredit(scoringMock);
        });

        lenient().when(scoringMock.getMiddleName()).thenReturn("ThisMiddleNameIsWayTooLongToBeValid");
        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCredit(scoringMock);
        });

        lenient().when(scoringMock.getMiddleName()).thenReturn("Ivan#vich");
        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCredit(scoringMock);
        });
    }



    @Test
    void testAgeLessThan20ReturnsNull() {
        lenient().when(scoringMock.getBirthdate()).thenReturn(LocalDate.now().minusYears(1));
        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCredit(scoringMock);
        });
    }

    @Test
    void testAgeMoreThan65ReturnsNull() {
        lenient().when(scoringMock.getBirthdate()).thenReturn(LocalDate.now().plusYears(66));
        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCredit(scoringMock);
        });
    }

    @Test
    void testEmploymentNullReturnsNull() {
        lenient().when(scoringMock.getEmployment()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCredit(scoringMock);
        });
    }

    @Test
    void testEmploymentStatusUnemployedReturnsNull() {
        lenient().when(employmentMock.getEmploymentStatus()).thenReturn(EmploymentStatus.UNEMPLOYED);
        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCredit(scoringMock);
        });
    }



    @Test
    void testSalaryTooLowReturnsNull() {
        lenient().when(employmentMock.getEmploymentStatus()).thenReturn(EmploymentStatus.EMPLOYED);
        lenient().when(employmentMock.getSalary()).thenReturn(BigDecimal.valueOf(10000));
        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCredit(scoringMock);
        });
    }

    @Test
    void testEmploymentStatusBusinessOwnerAddsDelta() {
        lenient().when(employmentMock.getEmploymentStatus()).thenReturn(EmploymentStatus.BUSINESS_OWNER);
        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCredit(scoringMock);
        });
    }

    @Test
    void testEmploymentStatusSelfEmployedAddsDelta() {
        lenient().when(employmentMock.getEmploymentStatus()).thenReturn(EmploymentStatus.SELF_EMPLOYED);
        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCredit(scoringMock);
        });
    }

}

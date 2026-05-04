package com.bobocode.tudaleasing.service;

import com.bobocode.tudaleasing.dto.LeasingCalculatorRequest;
import com.bobocode.tudaleasing.dto.LeasingCalculatorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("LeasingCalculatorService – unit tests")
class LeasingCalculatorServiceTest {

    private LeasingCalculatorService service;

    @BeforeEach
    void setUp() {
        service = new LeasingCalculatorService();
    }


    @Test
    @DisplayName("calculateLeasing: returns non-null netto and brutto payments")
    void calculateLeasing_returnsPayments() {
        LeasingCalculatorRequest req = buildRequest(new BigDecimal("100000"), 48, 20, 10);

        LeasingCalculatorResponse response = service.calculateLeasing(req);

        assertThat(response.getMonthlyPaymentNetto()).isNotNull().isPositive();
        assertThat(response.getMonthlyPaymentBrutto()).isNotNull().isPositive();
    }

    @Test
    @DisplayName("calculateLeasing: brutto = netto * 1.23")
    void calculateLeasing_bruttoIsNettoWithVat() {
        LeasingCalculatorRequest req = buildRequest(new BigDecimal("200000"), 60, 18, 10);

        LeasingCalculatorResponse response = service.calculateLeasing(req);

        BigDecimal expectedBrutto = response.getMonthlyPaymentNetto()
                .multiply(new BigDecimal("1.23"))
                .setScale(2, java.math.RoundingMode.HALF_UP);
        assertThat(response.getMonthlyPaymentBrutto())
                .isCloseTo(expectedBrutto, within(new BigDecimal("0.01")));
    }

    @Test
    @DisplayName("calculateLeasing: initial fee amount equals carPrice * initialFeePercent / 100")
    void calculateLeasing_correctInitialFeeAmount() {
        BigDecimal carPrice = new BigDecimal("100000");
        LeasingCalculatorRequest req = buildRequest(carPrice, 48, 20, 10);

        LeasingCalculatorResponse response = service.calculateLeasing(req);

        assertThat(response.getInitialFeeAmount())
                .isCloseTo(new BigDecimal("20000"), within(new BigDecimal("1")));
    }

    @Test
    @DisplayName("calculateLeasing: buyout amount equals carPrice * buyoutPercent / 100")
    void calculateLeasing_correctBuyoutAmount() {
        BigDecimal carPrice = new BigDecimal("228500");
        LeasingCalculatorRequest req = buildRequest(carPrice, 60, 18, 10);

        LeasingCalculatorResponse response = service.calculateLeasing(req);

        assertThat(response.getBuyoutAmount())
                .isCloseTo(new BigDecimal("22850"), within(new BigDecimal("1")));
    }

    @Test
    @DisplayName("calculateLeasing: real Polish example – netto ~ 3140 zł")
    void calculateLeasing_realWorldExample() {
        LeasingCalculatorRequest req = buildRequest(new BigDecimal("228500"), 60, 18, 10);

        LeasingCalculatorResponse response = service.calculateLeasing(req);

        assertThat(response.getMonthlyPaymentNetto())
                .isCloseTo(new BigDecimal("3140"), within(new BigDecimal("5")));
    }

    @Test
    @DisplayName("calculateLeasing: longer period results in lower monthly payment")
    void calculateLeasing_longerPeriodLowerPayment() {
        LeasingCalculatorRequest req24 = buildRequest(new BigDecimal("100000"), 24, 20, 5);
        LeasingCalculatorRequest req60 = buildRequest(new BigDecimal("100000"), 60, 20, 5);

        BigDecimal payment24 = service.calculateLeasing(req24).getMonthlyPaymentNetto();
        BigDecimal payment60 = service.calculateLeasing(req60).getMonthlyPaymentNetto();

        assertThat(payment24).isGreaterThan(payment60);
    }


    @Test
    @DisplayName("calculateCredit: returns non-null credit payment")
    void calculateCredit_returnsPayment() {
        LeasingCalculatorRequest req = buildRequest(new BigDecimal("80000"), 36, 15, 0);

        LeasingCalculatorResponse response = service.calculateCredit(req);

        assertThat(response.getMonthlyPaymentCredit()).isNotNull().isPositive();
    }

    @Test
    @DisplayName("calculateCredit: RRSO equals 12.18%")
    void calculateCredit_rrsoIs1218() {
        LeasingCalculatorRequest req = buildRequest(new BigDecimal("50000"), 24, 10, 0);

        LeasingCalculatorResponse response = service.calculateCredit(req);

        assertThat(response.getRrso())
                .isCloseTo(new BigDecimal("12.18"), within(new BigDecimal("0.01")));
    }

    @Test
    @DisplayName("calculateCredit: correct initial fee amount")
    void calculateCredit_correctInitialFeeAmount() {
        BigDecimal carPrice = new BigDecimal("60000");
        LeasingCalculatorRequest req = buildRequest(carPrice, 36, 25, 0);

        LeasingCalculatorResponse response = service.calculateCredit(req);

        assertThat(response.getInitialFeeAmount())
                .isCloseTo(new BigDecimal("15000"), within(new BigDecimal("1")));
    }

    @Test
    @DisplayName("calculateCredit: zero initial fee means full price is financed")
    void calculateCredit_zeroInitialFee() {
        BigDecimal carPrice = new BigDecimal("50000");
        LeasingCalculatorRequest withFee = buildRequest(carPrice, 24, 20, 0);
        LeasingCalculatorRequest noFee  = buildRequest(carPrice, 24, 0,  0);

        BigDecimal withFeePayment = service.calculateCredit(withFee).getMonthlyPaymentCredit();
        BigDecimal noFeePayment   = service.calculateCredit(noFee).getMonthlyPaymentCredit();

        assertThat(noFeePayment).isGreaterThan(withFeePayment);
    }

    private LeasingCalculatorRequest buildRequest(BigDecimal carPrice, int months,
                                                  int initialPct, int buyoutPct) {
        LeasingCalculatorRequest req = new LeasingCalculatorRequest();
        req.setCarPrice(carPrice);
        req.setPeriodMonths(months);
        req.setInitialFeePercent(initialPct);
        req.setBuyoutPercent(buyoutPct);
        return req;
    }
}


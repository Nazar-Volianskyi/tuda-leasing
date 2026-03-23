package com.bobocode.tudaleasing.service;

import com.bobocode.tudaleasing.dto.LeasingCalculatorRequest;
import com.bobocode.tudaleasing.dto.LeasingCalculatorResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Service
public class LeasingCalculatorService {

    /**
     * Annual leasing interest rate (netto).
     *
     * Verified against real Polish leasing calculator:
     *   car=228500, period=60, initial=18%, buyout=10% → PMT netto ≈ 3140 zł
     *   Formula: annuity on (carPrice - initialFee - buyoutAmount), rate=5.5%
     */
    private static final BigDecimal LEASING_ANNUAL_RATE = new BigDecimal("0.055"); // 5.5%

    /**
     * Annual credit interest rate (matches RRSO 17.18% on screenshot).
     */
    private static final BigDecimal CREDIT_ANNUAL_RATE = new BigDecimal("0.1218"); // 12.18%

    private static final BigDecimal VAT_RATE    = new BigDecimal("0.23");
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final BigDecimal ONE         = BigDecimal.ONE;
    private static final int        SCALE       = 10;
    private static final RoundingMode RM        = RoundingMode.HALF_UP;

    /**
     * Calculate monthly leasing payment (standard Polish leasing).
     *
     * Formula (netto):
     *   financed = carPrice - initialFee - buyoutAmount
     *   PMT      = financed × r / (1 − (1+r)^−n)
     *   Gross    = PMT × (1 + VAT)
     *
     * Note: buyout is subtracted directly (not discounted to PV).
     * This matches real Polish leasing calculators.
     */
    public LeasingCalculatorResponse calculateLeasing(LeasingCalculatorRequest req) {
        BigDecimal carPrice  = req.getCarPrice();
        int        n         = req.getPeriodMonths();
        BigDecimal initial   = percent(carPrice, req.getInitialFeePercent());
        BigDecimal buyout    = percent(carPrice, req.getBuyoutPercent());
        BigDecimal financed  = carPrice.subtract(initial).subtract(buyout);

        BigDecimal pmtNetto  = annuity(financed, LEASING_ANNUAL_RATE, n);
        BigDecimal pmtBrutto = pmtNetto.multiply(ONE.add(VAT_RATE)).setScale(2, RM);

        return LeasingCalculatorResponse.builder()
                .monthlyPaymentNetto(pmtNetto)
                .monthlyPaymentBrutto(pmtBrutto)
                .initialFeeAmount(initial.setScale(0, RM))
                .buyoutAmount(buyout.setScale(0, RM))
                .build();
    }

    /**
     * Calculate monthly credit payment (brutto, standard annuity).
     *
     * Formula:
     *   principal = carPrice - initialFee
     *   PMT       = principal × r / (1 − (1+r)^−n)
     */
    public LeasingCalculatorResponse calculateCredit(LeasingCalculatorRequest req) {
        BigDecimal carPrice  = req.getCarPrice();
        int        n         = req.getPeriodMonths();
        BigDecimal initial   = percent(carPrice, req.getInitialFeePercent());
        BigDecimal principal = carPrice.subtract(initial);

        BigDecimal pmt  = annuity(principal, CREDIT_ANNUAL_RATE, n);
        BigDecimal rrso = CREDIT_ANNUAL_RATE.multiply(ONE_HUNDRED).setScale(2, RM);

        return LeasingCalculatorResponse.builder()
                .monthlyPaymentCredit(pmt)
                .initialFeeAmount(initial.setScale(0, RM))
                .rrso(rrso)
                .build();
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    /** Standard annuity: PMT = principal × r / (1 − (1+r)^−n) */
    private BigDecimal annuity(BigDecimal principal, BigDecimal annualRate, int n) {
        BigDecimal r          = annualRate.divide(new BigDecimal("12"), SCALE, RM);
        BigDecimal onePlusRn  = ONE.add(r).pow(n, new MathContext(SCALE));
        BigDecimal denominator = ONE.subtract(ONE.divide(onePlusRn, SCALE, RM));
        return principal.multiply(r).divide(denominator, 2, RM);
    }

    private BigDecimal percent(BigDecimal base, int pct) {
        return base.multiply(new BigDecimal(pct)).divide(ONE_HUNDRED, SCALE, RM);
    }
}

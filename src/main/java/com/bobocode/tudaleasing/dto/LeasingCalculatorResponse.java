package com.bobocode.tudaleasing.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class LeasingCalculatorResponse {
    private BigDecimal monthlyPaymentNetto;
    private BigDecimal monthlyPaymentBrutto;
    private BigDecimal monthlyPaymentCredit;
    private BigDecimal initialFeeAmount;

    private BigDecimal buyoutAmount;
    private BigDecimal rrso;
}

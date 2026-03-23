package com.bobocode.tudaleasing.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class LeasingCalculatorRequest {

    private BigDecimal carPrice;
    private int periodMonths;
    private int initialFeePercent;
    private int buyoutPercent;
}

package com.bobocode.tudaleasing.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CarFiltersDto {
    private List<String> brands;
    private List<String> models;
    private List<String> categories;
    private List<String> colors;
    private List<Integer> years;
    private List<String> fuelTypes;
    private List<String> gearboxes;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    private List<String> bodyTypes;
    private List<Integer> doors;
    private List<Integer> seats;
    private List<BigDecimal> engineCapacities;
    private List<Integer> enginePowers;
    private List<String> driveTypes;
    private List<Integer> maxSpeeds;
}
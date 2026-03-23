package com.bobocode.tudaleasing.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CarFilterRequest {

    private String brand;
    private String model;
    private String category;
    private String color;
    private Integer yearFrom;
    private Integer yearTo;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String fuelType;
    private String gearbox;

    private String bodyType;
    private Integer doors;
    private Integer seats;
    private BigDecimal engineCapacityFrom;
    private BigDecimal engineCapacityTo;
    private Integer enginePowerFrom;
    private Integer enginePowerTo;
    private String driveType;
    private Integer maxSpeedFrom;
    private Integer maxSpeedTo;
}
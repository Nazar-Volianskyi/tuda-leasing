package com.bobocode.tudaleasing.dto;

import java.math.BigDecimal;

public record CarSpecDto(
        String bodyType,
        Long doors,
        Long seats,
        BigDecimal engineCapacity,
        Long enginePower,
        String fuelType,
        String gearbox,
        String driveType,
        Integer maxSpeed,
        BigDecimal acceleration0100,
        Long fuelTankCapacity,
        Long trunkCapacity,
        Long weight
) {}

package com.bobocode.tudaleasing.dto;

import java.math.BigDecimal;
import java.util.List;

public record CarDetailsDto(
        Long id,
        String brand,
        String model,
        String category,
        String color,
        Long year,
        BigDecimal price,
        String description,
        Boolean available,
        List<String> images,
        CarSpecDto specs
) {}

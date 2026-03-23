package com.bobocode.tudaleasing.dto;

import java.math.BigDecimal;

public record CarCatalogDto(
        Long id,
        String brand,
        String model,
        String category,
        String color,
        Long year,
        BigDecimal price,
        String fuelType,
        String gearbox,
        String imageUrl
) { }

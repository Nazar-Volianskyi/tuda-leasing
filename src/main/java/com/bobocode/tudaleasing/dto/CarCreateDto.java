package com.bobocode.tudaleasing.dto;

import java.math.BigDecimal;
import java.util.List;

public record CarCreateDto(
        Long modelId,
        Long categoryId,
        Long colorId,
        Long year,
        BigDecimal fullPrice,
        String description,
        Boolean available,
        CarSpecDto specs,
        List<CarImageDto> images
) {
}

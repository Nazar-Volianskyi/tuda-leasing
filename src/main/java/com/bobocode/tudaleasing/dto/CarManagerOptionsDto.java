package com.bobocode.tudaleasing.dto;

import java.util.List;

public record CarManagerOptionsDto(
        List<LookupOptionDto> brands,
        List<LookupOptionDto> models,
        List<LookupOptionDto> categories,
        List<LookupOptionDto> colors
) {
}



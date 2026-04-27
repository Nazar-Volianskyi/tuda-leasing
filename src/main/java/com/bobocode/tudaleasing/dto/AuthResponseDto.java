package com.bobocode.tudaleasing.dto;

public record AuthResponseDto(
        String token,
        String email,
        String role
) {
}


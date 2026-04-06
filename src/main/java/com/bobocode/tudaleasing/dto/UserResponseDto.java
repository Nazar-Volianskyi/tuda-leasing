package com.bobocode.tudaleasing.dto;

import com.bobocode.tudaleasing.entity.enums.Role;

public record UserResponseDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        Role role
) {}
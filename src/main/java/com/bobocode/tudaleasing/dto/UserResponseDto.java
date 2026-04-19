package com.bobocode.tudaleasing.dto;

import com.bobocode.tudaleasing.entity.enums.Role;
import java.io.Serializable;

public record UserResponseDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        Role role
) implements Serializable {
    private static final long serialVersionUID = 1L;
}
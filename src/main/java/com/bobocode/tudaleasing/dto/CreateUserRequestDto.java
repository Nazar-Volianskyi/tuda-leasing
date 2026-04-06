package com.bobocode.tudaleasing.dto;

import com.bobocode.tudaleasing.entity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequestDto(@NotBlank @Email String email,
                                   @NotBlank String password,
                                   @NotBlank String firstName,
                                   @NotBlank String lastName,
                                   @NotNull Role role) {
}

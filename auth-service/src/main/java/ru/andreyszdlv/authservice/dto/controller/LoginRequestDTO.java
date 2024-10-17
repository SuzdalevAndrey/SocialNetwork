package ru.andreyszdlv.authservice.dto.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
        @NotBlank(message = "{data.user.email.is_empty}")
        @Email(message = "{data.user.email.is_not_valid}")
        String email,

        @NotBlank(message = "{data.user.password.is_empty}")
        @Size(min = 6, message = "{data.user.password.is_not_valid}")
        String password) {
}

package ru.andreyszdlv.authservice.dto.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateVerifiCodeRequestDTO(
        @NotBlank(message = "{data.user.email.is_empty}")
        @Email(message = "{data.user.email.is_not_valid}")
        String email
) {
}

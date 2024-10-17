package ru.andreyszdlv.authservice.dto.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConfirmEmailRequestDTO (
        @NotBlank(message = "{data.user.email.is_empty}")
        @Email(message = "{data.user.email.is_not_valid}")
        String email,

        @Size(min = 6, max = 6, message = "{data.code_verification.is_not_valid}")
        String code
){}

package ru.andreyszdlv.authservice.dto.controllerDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ConfirmEmailRequestDTO (
        @NotBlank(message = "{data.user.email.is_empty}")
        @Email(message = "{data.user.email.is_not_valid}")
        String email,

        @NotBlank(message = "{data.code_verification.is_empty}")
        String code
){}

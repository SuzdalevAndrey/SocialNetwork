package ru.andreyszdlv.userservice.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateEmailRequestDTO(
        @NotBlank(message = "{data.user.email.is_empty}")
        @Email(message = "{data.user.email.is_not_valid}")
        String email) {
}

package ru.andreyszdlv.userservice.dto.controllerDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateEmailRequestDTO(
        @NotBlank(message = "{data.user.email.is_empty}")
        @Email(message = "{data.user.email.is_not_valid}")
        String email) {
}

package ru.andreyszdlv.userservice.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequestDTO(
        @NotBlank(message = "{data.user.password.is_empty}")
        @Size(min = 6, message = "{data.user.password.is_not_valid}")
        String oldPassword,

        @NotBlank(message = "{data.user.password.is_empty}")
        @Size(min = 6, message = "{data.user.password.is_not_valid}")
        String newPassword
) {
}

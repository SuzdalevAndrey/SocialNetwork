package ru.andreyszdlv.userservice.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequestDTO(
        @NotBlank
        @Size(min = 6)
        String oldPassword,

        @NotBlank
        @Size(min = 6)
        String newPassword
) {
}

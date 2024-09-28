package ru.andreyszdlv.userservice.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 6)
        String password) {
}

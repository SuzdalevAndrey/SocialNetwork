package ru.andreyszdlv.userservice.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDTO(

        @NotBlank
        String refreshToken) {
}

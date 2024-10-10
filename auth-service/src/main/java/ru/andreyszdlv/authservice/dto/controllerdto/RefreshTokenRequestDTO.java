package ru.andreyszdlv.authservice.dto.controllerdto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDTO(

        @NotBlank(message = "{data.token.is_empty}")
        String refreshToken
) { }

package ru.andreyszdlv.authservice.controller.dto;

public record RefreshTokenResponseDTO(String accessToken, String refreshToken) {
}

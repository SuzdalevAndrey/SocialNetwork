package ru.andreyszdlv.authservice.dto.controller;

public record RefreshTokenResponseDTO(String accessToken, String refreshToken) {
}

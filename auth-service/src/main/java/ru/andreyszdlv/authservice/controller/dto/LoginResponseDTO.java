package ru.andreyszdlv.authservice.controller.dto;

public record LoginResponseDTO(String email, String accessToken, String refreshToken) {
}

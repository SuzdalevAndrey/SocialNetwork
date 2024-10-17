package ru.andreyszdlv.authservice.dto.controller;

public record LoginResponseDTO(String accessToken, String refreshToken) {
}

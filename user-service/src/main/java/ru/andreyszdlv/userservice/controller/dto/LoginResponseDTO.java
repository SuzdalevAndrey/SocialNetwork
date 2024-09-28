package ru.andreyszdlv.userservice.controller.dto;

public record LoginResponseDTO(String email, String accessToken, String refreshToken) {
}

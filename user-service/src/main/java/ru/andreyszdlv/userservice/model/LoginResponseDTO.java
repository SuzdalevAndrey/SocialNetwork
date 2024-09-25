package ru.andreyszdlv.userservice.model;

public record LoginResponseDTO(String email, String accessToken, String refreshToken) {
}

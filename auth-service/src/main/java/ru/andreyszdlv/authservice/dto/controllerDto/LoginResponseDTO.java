package ru.andreyszdlv.authservice.dto.controllerDto;

public record LoginResponseDTO(String email, String accessToken, String refreshToken) {
}

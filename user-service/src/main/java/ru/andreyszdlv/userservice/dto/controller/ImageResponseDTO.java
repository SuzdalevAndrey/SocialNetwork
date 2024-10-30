package ru.andreyszdlv.userservice.dto.controller;

public record ImageResponseDTO(
        String contentType,
        byte[] content
) {
}

package ru.andreyszdlv.imageservice.dto.controller;

public record ImageResponseDTO(
        String contentType,
        byte[] content
) {
}

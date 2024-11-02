package ru.andreyszdlv.imageservice.dto;

public record ImageResponseDTO(
        String contentType,
        byte[] content
) {
}

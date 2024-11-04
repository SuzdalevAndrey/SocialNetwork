package ru.andreyszdlv.userservice.dto.controller;

import lombok.Builder;

@Builder
public record ImageResponseDTO(
        String contentType,
        byte[] content
) {
}

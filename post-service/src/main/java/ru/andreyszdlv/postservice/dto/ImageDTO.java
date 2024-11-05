package ru.andreyszdlv.postservice.dto;

import lombok.Builder;

@Builder
public record ImageDTO(
        String contentType,
        byte[] content
) {
}

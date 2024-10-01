package ru.andreyszdlv.postservice.controller.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentRequestDTO(
        @NotBlank(message = "")
        @Size(max = 500, message = "")
        String content
) {
}

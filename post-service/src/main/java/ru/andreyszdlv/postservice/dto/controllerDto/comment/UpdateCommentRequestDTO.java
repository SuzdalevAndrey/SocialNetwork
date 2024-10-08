package ru.andreyszdlv.postservice.dto.controllerDto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCommentRequestDTO(
        @NotBlank(message = "{error.comment.content.is_empty}")
        @Size(max = 500, message = "{error.comment.content.is_not_valid}")
        String content
) {
}
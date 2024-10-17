package ru.andreyszdlv.postservice.dto.controller.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePostRequestDTO(
       @NotBlank(message = "{error.post.content.is_empty}")
       @Size(min = 1, max = 1000, message = "{error.post.content.is_not_valid}")
       String content
) {}

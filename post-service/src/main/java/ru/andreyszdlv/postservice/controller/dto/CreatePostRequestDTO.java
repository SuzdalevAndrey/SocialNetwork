package ru.andreyszdlv.postservice.controller.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreatePostRequestDTO(
       @NotBlank(message = "{error.post.content.is_empty}")
       @Size(min = 1, max = 1000, message = "{error.post.content.is_not_valid}")
       String content
) {}

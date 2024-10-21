package ru.andreyszdlv.postservice.dto.controller.comment;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentResponseDTO(
        long id,

        String content,

        LocalDateTime dateCreate,

        long userId,

        long postId
) { }

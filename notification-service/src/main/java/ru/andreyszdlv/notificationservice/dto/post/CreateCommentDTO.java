package ru.andreyszdlv.notificationservice.dto.post;

public record CreateCommentDTO(
        String email,
        String nameCommentAuthor,
        String content
) { }
package ru.andreyszdlv.postservice.dto.controller.post;

import lombok.Builder;
import ru.andreyszdlv.postservice.model.Comment;
import ru.andreyszdlv.postservice.model.Like;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostResponseDTO(
        long id,

        String content,

        long numberViews,

        LocalDateTime dateCreate,

        long userId,

        List<Like> likes,

        List<Comment> comments,

        List<String> imageIds
) {}

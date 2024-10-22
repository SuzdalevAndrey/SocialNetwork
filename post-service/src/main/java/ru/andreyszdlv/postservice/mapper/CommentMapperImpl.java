package ru.andreyszdlv.postservice.mapper;

import org.springframework.stereotype.Component;
import ru.andreyszdlv.postservice.dto.controller.comment.CommentResponseDTO;
import ru.andreyszdlv.postservice.model.Comment;

@Component
public class CommentMapperImpl implements CommentMapper{
    @Override
    public CommentResponseDTO commentToCommentReponseDTO(Comment comment) {
        if(comment == null)
            return null;

        return CommentResponseDTO
                .builder()
                .id(comment.getId())
                .userId(comment.getUserId())
                .content(comment.getContent())
                .dateCreate(comment.getDateCreate())
                .postId(comment.getPostId())
                .build();
    }
}

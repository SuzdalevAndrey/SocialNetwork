package ru.andreyszdlv.postservice.mapper;

import org.springframework.stereotype.Component;
import ru.andreyszdlv.postservice.dto.controller.comment.CommentResponseDTO;
import ru.andreyszdlv.postservice.model.Comment;

@Component
public interface CommentMapper {

    CommentResponseDTO commentToCommentReponseDTO(Comment comment);

}

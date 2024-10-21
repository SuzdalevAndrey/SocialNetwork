package ru.andreyszdlv.postservice.mapper;

import org.springframework.stereotype.Component;
import ru.andreyszdlv.postservice.dto.controller.post.PostResponseDTO;
import ru.andreyszdlv.postservice.model.Post;

@Component
public class PostMapperImpl implements PostMapper{
    @Override
    public PostResponseDTO postToPostResponseDTO(Post post) {
        return PostResponseDTO
                .builder()
                .id(post.getId())
                .userId(post.getUserId())
                .numberViews(post.getNumberViews())
                .content(post.getContent())
                .dateCreate(post.getDateCreate())
                .comments(post.getComments())
                .likes(post.getLikes())
                .build();
    }
}

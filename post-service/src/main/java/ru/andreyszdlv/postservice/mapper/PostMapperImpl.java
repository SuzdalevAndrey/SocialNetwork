package ru.andreyszdlv.postservice.mapper;

import org.springframework.stereotype.Component;
import ru.andreyszdlv.postservice.dto.controller.post.PostResponseDTO;
import ru.andreyszdlv.postservice.model.Post;

import java.util.List;

@Component
public class PostMapperImpl implements PostMapper{
    @Override
    public PostResponseDTO postToPostResponseDTO(Post post) {
        if(post == null)
            return null;

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

    @Override
    public List<PostResponseDTO> listPostToListPostResponseDTO(List<Post> posts) {
        if(posts == null)
            return null;
        return posts.stream()
                .map(this::postToPostResponseDTO)
                .toList();
    }
}

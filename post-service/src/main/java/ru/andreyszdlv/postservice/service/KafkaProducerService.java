package ru.andreyszdlv.postservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.postservice.dto.kafkaDto.CreateCommentKafkaDTO;
import ru.andreyszdlv.postservice.dto.kafkaDto.CreateLikeKafkaDTO;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    @Value("${spring.kafka.topic.nameTopicCreateLike}")
    private String nameTopicCreateLike;

    @Value("${spring.kafka.topic.nameTopicCreateComment}")
    private String nameTopicCreateComment;

    private final KafkaTemplate<String, CreateLikeKafkaDTO> kafkaTemplateCreateLike;

    private final KafkaTemplate<String, CreateCommentKafkaDTO> kafkaTemplateCreateComment;

    public void sendCreateLikeEvent(String email,
                                    String nameLikeAuthor){
        kafkaTemplateCreateLike.send(
                nameTopicCreateLike,
                new CreateLikeKafkaDTO(
                        email,
                        nameLikeAuthor
                )
        );
    }

    public void sendCreateCommentEvent(String email,
                                       String nameCommentAuthor,
                                       String content){
        kafkaTemplateCreateComment.send(
                nameTopicCreateComment,
                new CreateCommentKafkaDTO(
                        email,
                        nameCommentAuthor,
                        content
                )
        );
    }
}

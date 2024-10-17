package ru.andreyszdlv.postservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.postservice.dto.kafka.CreateCommentKafkaDTO;
import ru.andreyszdlv.postservice.dto.kafka.CreateLikeKafkaDTO;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    @Value("${spring.kafka.topic.name.create-like}")
    private String nameTopicCreateLike;

    @Value("${spring.kafka.topic.name.create-comment}")
    private String nameTopicCreateComment;

    private final KafkaTemplate<String, CreateLikeKafkaDTO> kafkaTemplateCreateLike;

    private final KafkaTemplate<String, CreateCommentKafkaDTO> kafkaTemplateCreateComment;

    public void sendCreateLikeEvent(String email,
                                    String nameLikeAuthor){
        log.info("Executing sendCreateLikeEvent in kafka with email: {}, name like author: {}",
                email,
                nameLikeAuthor);
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
        log.info("Executing sendCreateCommentEvent in kafka with email: {}, name comment author: {}",
                email,
                nameCommentAuthor);
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

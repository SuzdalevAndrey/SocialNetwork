package ru.andreyszdlv.postservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.postservice.dto.kafka.CreateCommentKafkaDTO;
import ru.andreyszdlv.postservice.dto.kafka.CreateLikeKafkaDTO;
import ru.andreyszdlv.postservice.props.KafkaProducerProperties;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaProducerProperties kafkaProducerProperties;

    private final KafkaTemplate<String, CreateLikeKafkaDTO> kafkaTemplateCreateLike;

    private final KafkaTemplate<String, CreateCommentKafkaDTO> kafkaTemplateCreateComment;

    public void sendCreateLikeEvent(String email){
        log.info("Executing sendCreateLikeEvent in kafka with email: {}",
                email
        );
        kafkaTemplateCreateLike.send(
                kafkaProducerProperties.getTopicNameCreateLike(),
                new CreateLikeKafkaDTO(
                        email
                )
        );
    }

    public void sendCreateCommentEvent(String email,
                                       String content){
        log.info("Executing sendCreateCommentEvent in kafka with email: {}",
                email
        );
        kafkaTemplateCreateComment.send(
                kafkaProducerProperties.getTopicNameCreateComment(),
                new CreateCommentKafkaDTO(
                        email,
                        content
                )
        );
    }
}

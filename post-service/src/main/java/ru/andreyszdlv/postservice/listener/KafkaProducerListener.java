package ru.andreyszdlv.postservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import ru.andreyszdlv.postservice.dto.kafka.CreateCommentKafkaDTO;
import ru.andreyszdlv.postservice.dto.kafka.CreateLikeKafkaDTO;
import ru.andreyszdlv.postservice.props.KafkaProducerProperties;

@RequiredArgsConstructor
@Slf4j
public class KafkaProducerListener {

    private final KafkaProducerProperties kafkaProducerProperties;

    private final KafkaTemplate<String, CreateLikeKafkaDTO> kafkaTemplateCreateLike;

    private final KafkaTemplate<String, CreateCommentKafkaDTO> kafkaTemplateCreateComment;

    @EventListener
    public void sendCreateLikeEvent(CreateLikeKafkaDTO likeDTO){
        log.info("Executing sendCreateLikeEvent in kafka with email: {}",
                likeDTO.email()
        );
        kafkaTemplateCreateLike.send(
                kafkaProducerProperties.getTopicNameCreateLike(),
                likeDTO
        );
    }

    @EventListener
    public void sendCreateCommentEvent(CreateCommentKafkaDTO commentDTO){
        log.info("Executing sendCreateCommentEvent in kafka with email: {}",
                commentDTO.email()
        );
        kafkaTemplateCreateComment.send(
                kafkaProducerProperties.getTopicNameCreateComment(),
                commentDTO
        );
    }
}

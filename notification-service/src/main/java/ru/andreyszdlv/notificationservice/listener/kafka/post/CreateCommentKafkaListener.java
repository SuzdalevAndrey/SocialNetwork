package ru.andreyszdlv.notificationservice.listener.kafka.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.notificationservice.dto.post.CreateCommentDTO;

@Component
@Slf4j
@RequiredArgsConstructor
public class CreateCommentKafkaListener {

    private final ObjectMapper mapper;

    private final ApplicationEventPublisher publisher;

    @KafkaListener(
            topics = "#{@kafkaConsumerProperties.topicNameCreateComment}",
            groupId = "#{@kafkaConsumerProperties.groupId}"
    )
    public void listen(String createCommentMessage) throws JsonProcessingException {
        log.info("Executing listen message in kafka");

        CreateCommentDTO createCommentDTO = mapper.readValue(
                createCommentMessage,
                CreateCommentDTO.class);

        log.info("Publish event about create comment event");
        publisher.publishEvent(createCommentDTO);
    }
}

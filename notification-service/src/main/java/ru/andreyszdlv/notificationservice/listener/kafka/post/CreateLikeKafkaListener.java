package ru.andreyszdlv.notificationservice.listener.kafka.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.notificationservice.dto.post.CreateLikeDTO;

@Component
@Slf4j
@RequiredArgsConstructor
public class CreateLikeKafkaListener {

    private final ObjectMapper mapper;

    private final ApplicationEventPublisher publisher;

    @KafkaListener(
            topics = "#{@kafkaConsumerProperties.topicNameCreateLike}",
            groupId = "#{@kafkaConsumerProperties.groupId}"
    )
    public void listen(String createLikeMessage) throws JsonProcessingException {
        log.info("Executing listen message in kafka");

        CreateLikeDTO createLikeDTO = mapper.readValue(
                createLikeMessage,
                CreateLikeDTO.class);

        log.info("Publish event about create like event");
        publisher.publishEvent(createLikeDTO);
    }
}

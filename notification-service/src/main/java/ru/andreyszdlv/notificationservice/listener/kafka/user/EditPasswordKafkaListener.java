package ru.andreyszdlv.notificationservice.listener.kafka.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.notificationservice.dto.user.EditPasswordKafkaDTO;

@Component
@Slf4j
@RequiredArgsConstructor
public class EditPasswordKafkaListener {

    private final ObjectMapper mapper;

    private final ApplicationEventPublisher publisher;

    @KafkaListener(topics = "${spring.kafka.topic.nameTopicEditPassword}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void listener(String editPasswordMessage)
            throws JsonProcessingException {

        EditPasswordKafkaDTO editPasswordKafka = mapper.readValue(editPasswordMessage,
                EditPasswordKafkaDTO.class);

        publisher.publishEvent(editPasswordKafka);

        log.info("Edit password user with email: {}", editPasswordKafka.email());
    }

}

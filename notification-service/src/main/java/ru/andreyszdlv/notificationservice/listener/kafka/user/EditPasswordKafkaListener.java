package ru.andreyszdlv.notificationservice.listener.kafka.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.notificationservice.dto.user.EditPasswordDTO;

@Component
@Slf4j
@RequiredArgsConstructor
public class EditPasswordKafkaListener {

    private final ObjectMapper mapper;

    private final ApplicationEventPublisher publisher;

    @KafkaListener(topics = "#{@kafkaConsumerProperties.topicNameEditPassword}",
            groupId = "#{@kafkaConsumerProperties.groupId}")
    public void listener(String editPasswordMessage) throws JsonProcessingException {
        log.info("Executing listen message in kafka");

        EditPasswordDTO editPassword = mapper.readValue(
                editPasswordMessage,
                EditPasswordDTO.class);

        publisher.publishEvent(editPassword);

        log.info("Edit password user with email: {}", editPassword.email());
    }
}

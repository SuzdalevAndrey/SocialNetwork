package ru.andreyszdlv.notificationservice.listener.kafka.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.notificationservice.dto.user.EditEmailKafkaDTO;

@Component
@Slf4j
@RequiredArgsConstructor
public class EditEmailKafkaListener {

    private final ObjectMapper mapper;

    private final ApplicationEventPublisher publisher;

    @KafkaListener(topics = "${spring.kafka.topic.nameTopicEditEmail}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String editEmailMessage)
            throws JsonProcessingException {

        EditEmailKafkaDTO editEmailKafka = mapper.readValue(editEmailMessage,
                EditEmailKafkaDTO.class);

        publisher.publishEvent(editEmailKafka);

        log.info("Edit oldEmail: {} on newEmail: {}",
                editEmailKafka.oldEmail(),
                editEmailKafka.newEmail());
    }
}
